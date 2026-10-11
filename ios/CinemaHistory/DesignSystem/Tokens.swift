import SwiftUI

/// Tokens do desenho aprovado (prancha K1 · Fundamentos). Nenhuma tela usa valor solto:
/// cor, tipografia e vidro saem daqui. A cor da era é só acento, nunca fundo de tela.
enum Paleta {
    static let tinta = Color(hex: 0x17130F)
    static let cartao = Color(hex: 0x2A231C)
    static let papel = Color(hex: 0xF5F2EC)
    static let ambar = Color(hex: 0xE8B24A)
    static let vermelho = Color(hex: 0xE5484D)
    static let textoSecundario = Color(hex: 0x6E655A)
    static let textoSobreTinta = Color(hex: 0xA39887)

    /// Cor de acento de cada uma das 8 eras (índice 0 = era 1).
    static let eras: [Color] = [
        Color(hex: 0xE5484D), Color(hex: 0xA855C8), Color(hex: 0xF08A24), Color(hex: 0x4C5FD5),
        Color(hex: 0x0E93A6), Color(hex: 0x138A72), Color(hex: 0x2A7FD4), Color(hex: 0xD9541E),
    ]

    static func era(_ id: Int) -> Color {
        eras[min(max(id, 1), eras.count) - 1]
    }
}

enum Tipografia {
    /// SF Pro pesada nos títulos.
    static func titulo(_ tamanho: CGFloat) -> Font { .system(size: tamanho, weight: .heavy) }
    /// New York no texto corrido e nas citações.
    static func texto(_ tamanho: CGFloat) -> Font { .system(size: tamanho, weight: .regular, design: .serif) }
    /// SF Expanded em caixa alta nos rótulos.
    static func rotulo(_ tamanho: CGFloat = 11) -> Font {
        .system(size: tamanho, weight: .semibold).width(.expanded)
    }
}

extension Color {
    init(hex: UInt32) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255,
            opacity: 1
        )
    }
}
