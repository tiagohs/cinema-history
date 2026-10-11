import SwiftUI

/// Tela inicial da F0: prova que o SwiftUI lê as 8 eras do núcleo `shared` e usa os tokens do desenho.
/// O visual final do Início (hero, carrossel de eras, citação, Explore) entra na fase das telas.
struct InicioView: View {
    @State private var modelo = InicioViewModel()

    var body: some View {
        ZStack {
            Paleta.tinta.ignoresSafeArea()
            switch modelo.estado {
            case .carregando:
                ProgressView().tint(Paleta.ambar)
            case .erro(let mensagem):
                Text(mensagem)
                    .font(Tipografia.texto(17))
                    .foregroundStyle(Paleta.textoSobreTinta)
                    .padding()
            case .pronto(let eras):
                ScrollView {
                    VStack(alignment: .leading, spacing: 14) {
                        Text("HISTORY OF CINEMA")
                            .font(Tipografia.rotulo())
                            .tracking(2.6)
                            .foregroundStyle(Paleta.ambar)
                        ForEach(eras) { era in
                            EraCartao(era: era)
                        }
                    }
                    .padding(20)
                }
            }
        }
        .preferredColorScheme(.dark)
        .task { modelo.carregar() }
    }
}

private struct EraCartao: View {
    let era: InicioViewModel.EraResumo

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Circle().fill(Paleta.era(era.id)).frame(width: 10, height: 10).padding(.top, 7)
            VStack(alignment: .leading, spacing: 4) {
                Text(era.subtitulo.uppercased())
                    .font(Tipografia.rotulo(10))
                    .foregroundStyle(Paleta.textoSobreTinta)
                Text(era.titulo)
                    .font(Tipografia.titulo(22))
                    .foregroundStyle(Paleta.papel)
            }
            Spacer(minLength: 0)
        }
        .padding(16)
        .background(Paleta.cartao, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
        .accessibilityElement(children: .combine)
    }
}

#Preview { InicioView() }
