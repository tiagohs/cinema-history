# Amostra para testar vozes (pt-BR)

~900 caracteres, ~1 minuto. Tem nomes franceses, um título em português, números por extenso,
uma citação (segunda voz) e um trecho mais dramático — o que mais diferencia uma voz da outra.

## Texto (narrador)

Vinte e oito de dezembro de mil oitocentos e noventa e cinco. Num salão do Grand Café, em Paris,
os irmãos Auguste e Louis Lumière cobraram ingresso, pela primeira vez, para exibir filmes a uma
plateia. Poucas semanas depois, diante de A chegada de um trem à estação de La Ciotat, parte do
público — diz a lenda — se encolheu na cadeira, como se a locomotiva fosse atravessar a tela.

Era o começo de uma arte que, em pouco mais de um século, atravessaria guerras, ganharia som e
cor, trocaria a película pelo digital e iria parar na palma da mão. Cento e trinta anos depois
daquela sessão, um filme brasileiro, Ainda Estou Aqui, levaria o Oscar de Melhor Filme
Internacional.

## Citação (segunda voz)

Quando você supera a barreira de uma polegada das legendas, vai conhecer muitos outros filmes
incríveis.

## Texto (narrador, fecho)

Disse o diretor sul-coreano Bong Joon Ho, no Globo de Ouro de dois mil e vinte.

---

## Como testar sem programar
- **Google Cloud (Chirp 3 HD):** página do Text-to-Speech no console do Google Cloud → "Try it" /
  Speech studio; idioma Português (Brasil); teste as vozes "pt-BR-Chirp3-HD-…" (sugestões em
  `../vozes.json`: Charon para narrador, Gacrux para citação). Cole cada parte separadamente.
- **Gemini TTS:** Google AI Studio → "Generate speech" (modo com dois locutores). Dá para dar
  instrução de estilo, por exemplo: "Narre em tom de documentário, calmo e caloroso, ritmo
  médio; a citação em voz diferente, mais próxima e pausada."
- Compare 3 vozes de narrador ouvindo: pronúncia de "Lumière", "La Ciotat", "Grand Café";
  naturalidade dos números; cansaço depois de 1 minuto (imagine 15).
