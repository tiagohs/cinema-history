import Foundation
import Testing
import Shared
@testable import CinemaHistory

@MainActor
struct InicioViewModelTests {
    @Test func carregaAsOitoErasDoConteudoEmbutidoEmPortugues() {
        let modelo = InicioViewModel()
        modelo.carregar(codigoDoIdioma: "pt-BR")
        guard case .pronto(let eras) = modelo.estado else {
            Issue.record("estado inesperado: \(modelo.estado)")
            return
        }
        #expect(eras.count == 8)
        #expect(eras.first?.titulo == "De 1895 a 1929")
        #expect(eras.map(\.id) == Array(1...8))
    }

    @Test func idiomaSemConteudoViraIngles() {
        let modelo = InicioViewModel()
        modelo.carregar(codigoDoIdioma: "fr-FR")
        guard case .pronto(let eras) = modelo.estado else {
            Issue.record("estado inesperado: \(modelo.estado)")
            return
        }
        #expect(eras.count == 8)
    }

    @Test func fonteVaziaViraErroEmVezDeFalhar() {
        let modelo = InicioViewModel(fonte: BundleContentSource(raiz: URL(fileURLWithPath: "/inexistente")))
        modelo.carregar(codigoDoIdioma: "pt")
        guard case .erro = modelo.estado else {
            Issue.record("deveria ser erro: \(modelo.estado)")
            return
        }
    }

    @Test func tokensDasErasCobremAsOitoCores() {
        #expect(Paleta.eras.count == 8)
    }
}
