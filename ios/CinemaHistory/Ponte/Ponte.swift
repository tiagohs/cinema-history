import Foundation
@preconcurrency import Shared

/// Helpers da ponte Kotlin↔Swift (exportação padrão do Kotlin/Native). Ver "Regras da ponte" no plano.

enum PonteErro: Error, Equatable {
    case falha(String)
}

/// Regra 2: aguarda um trabalho do núcleo exposto como `(aoTerminar, aoFalhar) -> Cancelavel`.
/// Cancelar a Task do Swift cancela a corrotina no Kotlin e lança `CancellationError`.
func aguardar<T>(
    _ iniciar: (_ aoTerminar: @escaping (T) -> Void, _ aoFalhar: @escaping (String) -> Void) -> Cancelavel
) async throws -> T {
    let espera = Espera<T>()
    return try await withTaskCancellationHandler {
        try await withCheckedThrowingContinuation { (continuacao: CheckedContinuation<T, Error>) in
            guard espera.comecar(continuacao) else { return }
            let alca = iniciar(
                { espera.terminar(.success($0)) },
                { espera.terminar(.failure(PonteErro.falha($0))) }
            )
            espera.guardar(alca)
        }
    } onCancel: {
        espera.cancelar()
    }
}

/// Regra 3: transforma `observar…(aoMudar) -> Cancelavel` num `AsyncStream` que guarda só o estado mais novo
/// e cancela a observação no Kotlin quando o consumidor para.
func observar<T>(_ iniciar: (_ aoMudar: @escaping (T) -> Void) -> Cancelavel) -> AsyncStream<T> {
    AsyncStream(bufferingPolicy: .bufferingNewest(1)) { continuacao in
        let alca = iniciar { continuacao.yield($0) }
        continuacao.onTermination = { _ in alca.cancelar() }
    }
}

/// Estado de uma espera: garante que a continuação é retomada uma única vez, mesmo com cancelamento em corrida.
private final class Espera<T>: @unchecked Sendable {
    private let trava = NSLock()
    private var continuacao: CheckedContinuation<T, Error>?
    private var alca: Cancelavel?
    private var cancelada = false

    /// Devolve false se a Task já foi cancelada (a continuação é retomada com erro e nada começa).
    func comecar(_ c: CheckedContinuation<T, Error>) -> Bool {
        trava.lock()
        if cancelada {
            trava.unlock()
            c.resume(throwing: CancellationError())
            return false
        }
        continuacao = c
        trava.unlock()
        return true
    }

    func guardar(_ a: Cancelavel) {
        trava.lock()
        if cancelada {
            trava.unlock()
            a.cancelar()
            return
        }
        alca = a
        trava.unlock()
    }

    func terminar(_ resultado: Result<T, Error>) {
        trava.lock()
        let c = continuacao
        continuacao = nil
        alca = nil
        trava.unlock()
        c?.resume(with: resultado)
    }

    func cancelar() {
        trava.lock()
        cancelada = true
        let c = continuacao
        continuacao = nil
        let a = alca
        alca = nil
        trava.unlock()
        a?.cancelar()
        c?.resume(throwing: CancellationError())
    }
}
