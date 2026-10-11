import Foundation
@preconcurrency import Shared

/// Preferências do núcleo guardadas no UserDefaults.
final class PreferenciasDoAparelho: NSObject, Preferencias {
    private let defaults: UserDefaults

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    func texto(chave: String) -> String? { defaults.string(forKey: chave) }

    func gravarTexto(chave: String, valor: String?) {
        if let valor { defaults.set(valor, forKey: chave) } else { defaults.removeObject(forKey: chave) }
    }

    func longo(chave: String, padrao: Int64) -> Int64 {
        (defaults.object(forKey: chave) as? NSNumber)?.int64Value ?? padrao
    }

    func gravarLongo(chave: String, valor: Int64) { defaults.set(NSNumber(value: valor), forKey: chave) }

    func logico(chave: String, padrao: Bool) -> Bool {
        (defaults.object(forKey: chave) as? NSNumber)?.boolValue ?? padrao
    }

    func gravarLogico(chave: String, valor: Bool) { defaults.set(valor, forKey: chave) }

    func remover(chave: String) { defaults.removeObject(forKey: chave) }
}

/// Relógio do aparelho: instante atual e o dia local no formato do Android (ano * 1000 + dia do ano).
final class RelogioDoAparelho: NSObject, Relogio {
    func agoraMs() -> Int64 { Int64(Date().timeIntervalSince1970 * 1000) }

    func diaLocal() -> Int32 {
        let calendario = Calendar.current
        let hoje = Date()
        let ano = calendario.component(.year, from: hoje)
        let dia = calendario.ordinality(of: .day, in: .year, for: hoje) ?? 1
        return Int32(ano * 1000 + dia)
    }
}

/// O núcleo do app, criado uma vez.
enum NucleoDoApp {
    static let pastaDoCache: URL = {
        let base = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
        return base.appendingPathComponent("conteudo-remoto", isDirectory: true)
    }()

    static var versaoDoApp: Int64 {
        Int64(Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? "1") ?? 1
    }

    static let compartilhado = Nucleo(
        embutido: BundleContentSource.padrao(),
        pastaDoCache: pastaDoCache.path,
        preferencias: PreferenciasDoAparelho(),
        relogio: RelogioDoAparelho(),
        versaoDoApp: versaoDoApp
    )
}
