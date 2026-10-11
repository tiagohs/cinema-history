import Foundation
import Shared

/// Lê o conteúdo local (a pasta `local` embutida no app, a mesma do Android) para o núcleo `shared`.
final class BundleContentSource: NSObject, ContentSource {
    private let raiz: URL

    init(raiz: URL) {
        self.raiz = raiz
    }

    static func padrao(bundle: Bundle = .main) -> BundleContentSource {
        let raiz = bundle.url(forResource: "local", withExtension: nil) ?? bundle.bundleURL.appendingPathComponent("local")
        return BundleContentSource(raiz: raiz)
    }

    func lerTexto(caminho: String) -> String? {
        try? String(contentsOf: raiz.appendingPathComponent(caminho), encoding: .utf8)
    }
}
