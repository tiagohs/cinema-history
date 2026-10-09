// Worker só de leitura: entrega os arquivos do bucket R2 com suporte a Range (avançar/voltar no player),
// ETag/304 e cache na borda da Cloudflare. Nenhum segredo aqui: o bucket fica privado, só este Worker lê.
const TYPES = { ogg: "audio/ogg", opus: "audio/ogg", json: "application/json; charset=utf-8" };
const LONG = "public, max-age=2592000, immutable"; // faixas: o nome muda quando o áudio muda (hash no manifest)
const SHORT = "public, max-age=3600"; // manifest.json: pode ser regerado

export default {
  async fetch(request, env, ctx) {
    if (request.method !== "GET" && request.method !== "HEAD") {
      return new Response("Method not allowed", { status: 405, headers: { Allow: "GET, HEAD" } });
    }
    const url = new URL(request.url);
    const key = decodeURIComponent(url.pathname.slice(1));
    if (!key || key.includes("..") || !/^[a-z]{2}\/main_\d+\/page_\d+\/[\w.-]+$/.test(key)) {
      return new Response("Not found", { status: 404 });
    }

    // cache da borda (só respostas completas; Range é recortado a partir dele)
    const cache = caches.default;
    const cacheKey = new Request(url.toString(), { method: "GET" });
    const range = request.headers.get("Range");
    if (!range) {
      const hit = await cache.match(cacheKey);
      if (hit) return request.method === "HEAD" ? new Response(null, hit) : hit;
    }

    const object = await env.AUDIO.get(key, {
      range: request.headers,
      onlyIf: request.headers,
    });
    if (object === null) return new Response("Not found", { status: 404 });

    const headers = new Headers();
    object.writeHttpMetadata(headers);
    headers.set("ETag", object.httpEtag);
    headers.set("Accept-Ranges", "bytes");
    headers.set("Access-Control-Allow-Origin", "*");
    const ext = key.split(".").pop();
    if (!headers.has("Content-Type") && TYPES[ext]) headers.set("Content-Type", TYPES[ext]);
    headers.set("Cache-Control", ext === "json" ? SHORT : LONG);

    // If-None-Match / If-Modified-Since atendidos: sem corpo
    if (!("body" in object)) return new Response(null, { status: 304, headers });

    if (range && object.range) {
      const { offset = 0, length = object.size - offset } = object.range;
      headers.set("Content-Range", `bytes ${offset}-${offset + length - 1}/${object.size}`);
      headers.set("Content-Length", String(length));
      return new Response(request.method === "HEAD" ? null : object.body, { status: 206, headers });
    }

    headers.set("Content-Length", String(object.size));
    const response = new Response(request.method === "HEAD" ? null : object.body, { status: 200, headers });
    if (request.method === "GET") ctx.waitUntil(cache.put(cacheKey, response.clone()));
    return response;
  },
};
