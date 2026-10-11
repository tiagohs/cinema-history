import Foundation
import Testing
import Shared
@testable import CinemaHistory

/// Protótipo da exportação padrão do Kotlin/Native (sem SKIE): cada teste cobre uma situação crítica do app.
/// Linhas "MEDIDA|..." vão para o resumo do CI.
private func medida(_ caso: String, _ texto: String) {
    print("MEDIDA|\(caso)|\(texto)")
}

private func agora() -> UInt64 { DispatchTime.now().uptimeNanoseconds }
private func ms(_ desde: UInt64) -> Double { Double(agora() - desde) / 1_000_000 }

private final class Sentinela {
    var valor = 0
}

private final class LojaFalsa: NSObject, LojaDeApoio {
    let aprovar: Bool
    init(aprovar: Bool) { self.aprovar = aprovar }
    func comprar(produto: String, completionHandler: @escaping (KotlinBoolean?, Error?) -> Void) {
        DispatchQueue.global().asyncAfter(deadline: .now() + 0.05) {
            completionHandler(KotlinBoolean(bool: self.aprovar), nil)
        }
    }
}

/// Guarda o estado observado vindo de outra thread.
private final class Caixa: @unchecked Sendable {
    private let trava = NSLock()
    private var _valores: [Int32] = []
    private var _principal: [Bool] = []
    func adicionar(_ v: Int32, principal: Bool) { trava.lock(); _valores.append(v); _principal.append(principal); trava.unlock() }
    var valores: [Int32] { trava.lock(); defer { trava.unlock() }; return _valores }
    var algumaNaPrincipal: Bool { trava.lock(); defer { trava.unlock() }; return _principal.contains(true) }
}

struct PonteSpikeTests {
    let ponte = PonteSpike()

    // S1 · Resultado genérico (sealed + genérico) x resultado concreto
    @Test func s1_resultados() {
        let generico = ponte.resultadoGenerico(ok: true)
        let sucesso = generico as? ResultadoSucesso<AnyObject>
        let itens = sucesso?.valor as? [String]
        #expect(itens == ["a", "b"])
        #expect(ponte.resultadoGenerico(ok: false) is ResultadoErro)
        let concreto = ponte.resultadoConcreto(ok: true)
        #expect(concreto.itens == ["a", "b"])
        #expect(ponte.resultadoConcreto(ok: false).erro == "falhou")
        medida("S1", "sealed+genérico exige cast (valor chega como AnyObject); resultado concreto chega tipado")
    }

    // S2 · os 16 tipos de bloco: o Swift não tem switch exaustivo; um teste garante que nenhum tipo fica sem tela
    @Test func s2_todosOsBlocosTemRenderizador() {
        let renderizadores: Set<String> = [
            "TEXT", "GIF", "VIDEO", "SLIDE", "IMAGE", "AUDIO_STREAM", "QUOTE", "BLOCK_SPECIAL", "LINK_SCREEN",
            "MOVIE_LIST", "PERSON_LIST", "RECOMENDATIONS", "AWARDS_NOMINEES", "MOVIE_LIST_SPECIAL", "TWITTER", "ESSAY",
        ]
        let tipos = ponte.todosOsTipos()
        #expect(tipos.count == 16)
        #expect(tipos.allSatisfy { renderizadores.contains($0.name) })
        let blocos = ponte.blocos()
        var reconhecidos = 0
        for bloco in blocos {
            switch bloco {
            case let t as BlocoTexto: reconhecidos += t.html.isEmpty ? 0 : 1
            case let v as BlocoVideo: reconhecidos += v.youtubeId.isEmpty ? 0 : 1
            case let c as BlocoCitacao: reconhecidos += c.autor.isEmpty ? 0 : 1
            case let i as BlocoImagem: reconhecidos += i.legenda == nil ? 1 : 0
            case let l as BlocoListaFilmes: reconhecidos += l.ids.count == 2 ? 1 : 0
            default: reconhecidos += renderizadores.contains(bloco.tipo.name) ? 1 : 0
            }
        }
        #expect(reconhecidos == 16)
        medida("S2", "16 tipos reconhecidos; exaustividade garantida por teste, não pelo compilador")
    }

    // S3 · suspend → async, e custo por chamada
    @Test func s3_suspendViraAsync() async throws {
        let inicio = agora()
        var soma: Int32 = 0
        for _ in 0..<20 { soma += try await ponte.somar(a: 2, b: 3).int32Value }
        #expect(soma == 100)
        let media = ms(inicio) / 20
        medida("S3", String(format: "await de suspend com delay(20ms): média %.1f ms (sobrecarga ≈ %.1f ms); retorno Int chega como KotlinInt", media, media - 20))
    }

    // S4a · cancelamento de Task do Swift chega ao Kotlin?
    @Test func s4a_cancelamentoNativoDoAsync() async throws {
        let p = ponte
        let tarefa = Task { try await p.tarefaLonga(duracaoMs: 3000) }
        try await Task.sleep(nanoseconds: 200_000_000)
        tarefa.cancel()
        try await Task.sleep(nanoseconds: 300_000_000)
        let depois = p.passosDaTarefaLonga
        try await Task.sleep(nanoseconds: 300_000_000)
        let maisTarde = p.passosDaTarefaLonga
        let parou = maisTarde == depois
        medida("S4a", "cancelar a Task do Swift parou a corrotina Kotlin: \(parou ? "SIM" : "NÃO") (passos \(depois) → \(maisTarde)); Kotlin viu CancellationException: \(p.tarefaLongaFoiCancelada ? "SIM" : "NÃO")")
    }

    // S4b · contorno: alça Cancelavel ligada ao cancelamento da Task
    @Test func s4b_cancelamentoComAlca() async throws {
        let p = ponte
        let alca = p.tarefaLongaCancelavel(duracaoMs: 3000) { _ in }
        try await Task.sleep(nanoseconds: 200_000_000)
        alca.cancelar()
        try await Task.sleep(nanoseconds: 200_000_000)
        let depois = p.passosDaTarefaLonga
        try await Task.sleep(nanoseconds: 300_000_000)
        #expect(p.passosDaTarefaLonga == depois)
        #expect(p.tarefaLongaFoiCancelada)
        #expect(!alca.ativo)
        medida("S4b", "alça Cancelavel para a corrotina na hora: SIM")
    }

    // S5 · erros do Kotlin chegam como Error do Swift (com @Throws)
    @Test func s5_errosViramErrorDoSwift() async {
        do {
            _ = try await ponte.falharComErro()
            Issue.record("deveria lançar")
        } catch {
            let mensagem = (error as NSError).localizedDescription
            #expect(mensagem.contains("Sem internet"))
            medida("S5", "erro de suspend chega como NSError: \"\(mensagem)\"")
        }
        do {
            _ = try ponte.falharSincrono()
            Issue.record("deveria lançar")
        } catch {
            #expect((error as NSError).localizedDescription.contains("JSON inválido"))
        }
    }

    // S6 · fluxo de estado observado pelo Swift (player, download, idioma, apoio)
    @Test func s6_observarEstado() async throws {
        let p = PonteSpike()
        let caixa = Caixa()
        let alca = p.observarPlayer { estado in caixa.adicionar(estado.sequencia, principal: Thread.isMainThread) }
        try await Task.sleep(nanoseconds: 100_000_000)
        p.emitir(quantos: 40, intervaloMs: 25)
        try await Task.sleep(nanoseconds: 1_600_000_000)
        let recebidos = caixa.valores
        #expect(recebidos.last == 40)
        p.emitir(quantos: 500, intervaloMs: 0)
        try await Task.sleep(nanoseconds: 500_000_000)
        let rajada = caixa.valores.count - recebidos.count
        #expect(caixa.valores.last == 500)
        alca.cancelar()
        let antes = caixa.valores.count
        p.emitir(quantos: 5, intervaloMs: 0)
        try await Task.sleep(nanoseconds: 200_000_000)
        #expect(caixa.valores.count == antes)
        medida("S6", "a cada 25 ms: \(recebidos.count - 1) de 40 estados entregues; rajada de 500 sem pausa: \(rajada) entregues (StateFlow guarda o último); callback na thread principal: \(caixa.algumaNaPrincipal ? "às vezes" : "nunca"); após cancelar: nada mais chega")
    }

    // S7 · chamadas de alta frequência Swift → Kotlin
    @Test func s7_altaFrequencia() async {
        let p = PonteSpike()
        var inicio = agora()
        var acumulado: Int64 = 0
        for i in 0..<100_000 { acumulado += Int64(p.trechoAtual(posicaoMs: Int64(i * 30))) }
        let porChamada = ms(inicio) * 1000 / 100_000
        #expect(acumulado > 0)
        inicio = agora()
        await withTaskGroup(of: Void.self) { grupo in
            for t in 0..<8 {
                grupo.addTask { for i in 0..<2_500 { p.salvarPosicao(capitulo: "c\(t)", posicaoMs: Int64(i)) } }
            }
        }
        let paralelo = ms(inicio)
        #expect(p.posicoesSalvas == 20_000)
        medida("S7", String(format: "trechoAtual: %.2f µs por chamada (100 mil); 20 mil salvarPosicao em 8 tarefas paralelas: %.0f ms, sem perda", porChamada, paralelo))
    }

    // S8 · threads: callback de outra thread e suspend chamado fora da principal
    @Test func s8_threads() async throws {
        let p = ponte
        let principal: Bool = await withCheckedContinuation { cont in
            p.chamarDeVolta { cont.resume(returning: Thread.isMainThread) }
        }
        let deFundo = try await Task.detached { try await p.somar(a: 1, b: 1).int32Value }.value
        let daPrincipal = try await MainActor.run { Task { try await p.somar(a: 2, b: 2).int32Value } }.value
        #expect(deFundo == 2)
        #expect(daPrincipal == 4)
        medida("S8", "callback do Kotlin chega na thread principal: \(principal ? "SIM" : "NÃO, precisa voltar para o MainActor"); suspend chamado de thread de fundo e da principal: OK")
    }

    // S9 · memória: closure guardada pelo Kotlin e liberação
    @Test func s9_memoria() async throws {
        let p = PonteSpike()
        weak var fraca: Sentinela?
        do {
            let s = Sentinela()
            fraca = s
            _ = p.registrar { s.valor += 1 }
        }
        p.coletarLixo()
        let presaEnquantoRegistrada = fraca != nil
        p.limparObservadores()
        for _ in 0..<5 { p.coletarLixo(); try await Task.sleep(nanoseconds: 50_000_000) }
        let liberada = fraca == nil

        // ciclo entre os dois mundos: objeto Swift guarda a ponte, e a ponte guarda uma closure que captura o objeto Swift
        final class Tela { var ponte: PonteSpike? }
        weak var telaFraca: Tela?
        do {
            let tela = Tela()
            let pp = PonteSpike()
            tela.ponte = pp
            _ = pp.registrar { _ = tela }
            telaFraca = tela
        }
        for _ in 0..<5 { p.coletarLixo(); try await Task.sleep(nanoseconds: 50_000_000) }
        let cicloColetado = telaFraca == nil
        #expect(presaEnquantoRegistrada)
        #expect(liberada)
        medida("S9", "closure presa enquanto registrada: \(presaEnquantoRegistrada ? "SIM" : "NÃO"); liberada após remover + GC: \(liberada ? "SIM" : "NÃO"); ciclo Swift↔Kotlin sem [weak self] coletado: \(cicloColetado ? "SIM" : "NÃO (vaza)")")
    }

    // S10 · porta implementada em Swift (StoreKit) com suspend
    @Test func s10_portaSwiftComSuspend() async throws {
        let aprovado = try await ServicoDeApoio(loja: LojaFalsa(aprovar: true)).apoiar(produto: "apoio_19")
        let recusado = try await ServicoDeApoio(loja: LojaFalsa(aprovar: false)).apoiar(produto: "apoio_19")
        #expect(aprovado == "apoiador")
        #expect(recusado == "sem-apoio")
        medida("S10", "interface Kotlin com suspend implementada em Swift (completionHandler): OK")
    }

    // S11 · desempenho com o maior capítulo real embutido
    @Test func s11_desempenhoDoConteudoReal() throws {
        let raiz = try #require(Bundle.main.url(forResource: "local", withExtension: nil))
        let paginas = raiz.appendingPathComponent("pt/pages")
        let arquivos = FileManager.default.enumerator(at: paginas, includingPropertiesForKeys: [.fileSizeKey])?
            .compactMap { $0 as? URL }.filter { $0.pathExtension == "json" } ?? []
        let maior = try #require(arquivos.max { a, b in
            ((try? a.resourceValues(forKeys: [.fileSizeKey]).fileSize) ?? 0) < ((try? b.resourceValues(forKeys: [.fileSizeKey]).fileSize) ?? 0)
        })
        let texto = try String(contentsOf: maior, encoding: .utf8)
        let p = PonteSpike()
        _ = p.contarBlocos(textoDoCapitulo: texto)
        let inicio = agora()
        var blocos: Int32 = 0
        for _ in 0..<20 { blocos = p.contarBlocos(textoDoCapitulo: texto) }
        let media = ms(inicio) / 20
        #expect(blocos > 0)
        medida("S11", String(format: "maior página (%@, %d KB, %d blocos): %.1f ms por leitura, incluindo passar o texto Swift→Kotlin", maior.lastPathComponent, texto.utf8.count / 1024, blocos, media))
    }

    // S12 · tipos primitivos, opcionais e coleções
    @Test func s12_tipos() {
        let t = ponte.tipos()
        #expect(t.inteiro == 7)
        #expect(t.longo == 9_000_000_000)
        #expect(t.opcional == nil)
        #expect(t.lista.map { $0.int32Value } == [1, 2, 3])
        #expect(t.mapa["a"]?.int32Value == 1)
        #expect(ponte.ecoarOpcional(valor: KotlinInt(int: 4))?.int32Value == 5)
        medida("S12", "Int→Int32, Long→Int64; Int? e Int dentro de List/Map chegam como KotlinInt (precisa .int32Value)")
    }
}
