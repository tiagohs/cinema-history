import Foundation
import Testing
@preconcurrency import Shared
@testable import CinemaHistory

/// O núcleo F1 visto do Swift, com o conteúdo real embutido no app.
struct NucleoTests {
    private func nucleo(_ suite: String = UUID().uuidString) -> Nucleo {
        let pasta = FileManager.default.temporaryDirectory.appendingPathComponent(suite, isDirectory: true)
        return Nucleo(
            embutido: BundleContentSource.padrao(),
            pastaDoCache: pasta.path,
            preferencias: PreferenciasDoAparelho(defaults: UserDefaults(suiteName: suite)!),
            relogio: RelogioDoAparelho(),
            versaoDoApp: 1
        )
    }

    @Test func capituloCarregaForaDaThreadPrincipalComAguardar() async throws {
        let n = nucleo()
        let capitulo: Capitulo = try await aguardar { ok, falha in
            n.historia.carregarCapitulo(idioma: .pt, era: 1, numero: 1, aoTerminar: ok, aoFalhar: falha)
        }
        #expect(capitulo.blocos.count > 10)
        #expect(capitulo.blocos.first is Bloco.Texto)
        #expect((capitulo.blocos.first as? Bloco.Texto)?.html.hasPrefix("O cinema criou") == true)
    }

    @Test func todosOsCapitulosEmPortuguesAbremNoIOS() async throws {
        let n = nucleo()
        let inicio = DispatchTime.now().uptimeNanoseconds
        var capitulos = 0
        var blocos = 0
        for era in Int32(1)...8 {
            let sumario = n.historia.sumario(idioma: .pt, era: era)
            let itens = try #require(sumario.itens, "sumário \(era): \(sumario.erro ?? "")")
            for item in itens {
                let cap: Capitulo = try await aguardar { ok, falha in
                    n.historia.carregarCapitulo(idioma: .pt, era: era, numero: item.id, aoTerminar: ok, aoFalhar: falha)
                }
                capitulos += 1
                blocos += cap.blocos.count
                #expect(!cap.blocos.contains { $0 is Bloco.Desconhecido }, "bloco desconhecido em \(era)/\(item.id)")
                #expect(cap.blocos.allSatisfy { $0.tipo != .desconhecido })
            }
        }
        let ms = Double(DispatchTime.now().uptimeNanoseconds - inicio) / 1_000_000
        print("MEDIDA|F1|\(capitulos) capítulos e \(blocos) blocos lidos no iOS em \(Int(ms)) ms (\(String(format: "%.1f", ms / Double(capitulos))) ms por capítulo)")
        #expect(capitulos > 90)
    }

    @Test func explorarInicioELinks() throws {
        let n = nucleo()
        #expect((n.inicio.eras(idioma: .en).eras ?? []).count == 8)
        #expect((n.inicio.citacoes(idioma: .pt).citacoes ?? []).count == 2)
        #expect((n.explorar.glossario(idioma: .es).termos ?? []).count > 40)
        #expect((n.explorar.premios(idioma: .pt).premios ?? []).count == 7)
        #expect((n.explorar.linhaDoTempoCompleta(idioma: .pt).paginas ?? []).count == 8)
        #expect((n.explorar.diretores(idioma: .pt).diretores ?? []).count > 40)
        let oscar = try #require(n.explorar.premios(idioma: .pt).premios?.first { $0.idDosIndicados > 0 })
        let anos = try #require(n.explorar.anosDoPremio(idioma: .pt, idDosIndicados: oscar.idDosIndicados).anos)
        let primeiro = try #require(anos.first)
        #expect((n.explorar.indicadosDoAno(idioma: .pt, idDosIndicados: oscar.idDosIndicados, ano: primeiro.ano).ano?.categorias ?? []).count == Int(primeiro.categorias))
        let pessoa = n.destinoDoLink(endereco: "https://_{'type': 'screen', 'id': 11523, 'screen_type': 'person'}")
        #expect((pessoa as? DestinoDoLink.Pessoa)?.id == 11523)
        #expect(n.destinoDoLink(endereco: "https://_{type: 'screen', 'id': , 'screen_type': movie}") is DestinoDoLink.Invalido)
        #expect(n.idioma(escolhaDoApp: nil, preferidosDoAparelho: ["fr-FR", "es-419"]) == .es)
    }

    @Test func progressoEAnunciosComUserDefaults() {
        let n = nucleo()
        let p = n.progresso
        #expect(!p.onboardingConcluido)
        p.onboardingConcluido = true
        #expect(p.onboardingConcluido)
        p.marcarLido(era: 2, capitulo: 3)
        #expect(p.estaLido(era: 2, capitulo: 3))
        #expect(p.quantidadeDeLidos(era: 2) == 1)
        p.registrarLeitura(era: 4, capitulo: 7)
        #expect(p.ultimaEra == 4)
        let chave = ChaveDoCapitulo(idioma: .pt, era: 1, pagina: 2)
        p.salvarPosicaoDoAudio(chave: chave, faixa: "02", ms: 45_000)
        #expect(p.posicaoDoAudio(chave: chave)?.ms == 45_000)
        let anuncios = n.anuncios
        anuncios.registrarSessao()
        #expect(anuncios.sessoes == 1)
        #expect(!anuncios.aoPedirProximoCapitulo(apoiador: false, podePedirAnuncios: true), "primeira sessão nunca")
        #expect(Apoio.shared.ofertaDisponivel(paisDaLoja: "BRA", idioma: .pt))
        #expect(Apoio.shared.acessoAoAudio(apoiador: false, ofertaDisponivel: true) == .bloqueado)
    }

    @Test func manifestDoAudioNoIOS() throws {
        let json = #"{"title":"C","tracks":[{"id":"00","file":"00.ogg","duration_s":10,"marks":[{"seg":"a","t":0.25,"source_index":-1}]},{"id":"01","file":"01.ogg","file_m4a":"01.m4a","duration_s":30,"marks":[{"seg":"b","t":0.25,"source_index":0},{"seg":"c","t":12,"source_index":1}]}]}"#
        let chave = ChaveDoCapitulo(idioma: .pt, era: 1, pagina: 1)
        let m = try ManifestDoAudio.companion.ler(json: json, chave: chave, pasta: "https://a.test/pt/main_1/page_1/")
        #expect(m.faixas.count == 2)
        #expect(m.blocoEm(faixa: 1, posicaoMs: 12_000) == 1)
        #expect(m.urlDaFaixa(faixa: m.faixas[1]) == "https://a.test/pt/main_1/page_1/01.m4a")
        #expect(m.localizar(indiceDoBloco: 1)?.ms == 12_000)
    }

    @Test func sincronizacaoPrepararNaoUsaRede() {
        nucleo().sincronizacao.preparar()
    }
}
