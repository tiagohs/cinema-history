import Foundation
import Observation
import Shared

@MainActor
@Observable
final class InicioViewModel {
    enum Estado: Equatable {
        case carregando
        case pronto([EraResumo])
        case erro(String)
    }

    struct EraResumo: Equatable, Identifiable {
        let id: Int
        let titulo: String
        let subtitulo: String
        let descricao: String
    }

    private(set) var estado: Estado = .carregando
    private let api: InicioApi

    init(fonte: ContentSource = BundleContentSource.padrao()) {
        self.api = InicioApi(fonte: fonte)
    }

    func carregar(codigoDoIdioma: String = Locale.preferredLanguages.first ?? "en") {
        let idioma = api.idiomaDoAparelho(codigo: codigoDoIdioma)
        let resultado = api.eras(idioma: idioma)
        if let eras = resultado.eras {
            estado = .pronto(eras.map {
                EraResumo(id: Int($0.id), titulo: $0.titulo, subtitulo: $0.subtitulo, descricao: $0.descricao)
            })
        } else {
            estado = .erro(resultado.erro ?? "Conteúdo indisponível")
        }
    }
}
