//
//  PlayerView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import AVKit
import SwiftUI

struct PlayerView: UIViewControllerRepresentable {
    @Binding var videoURL: URL?

    private var player: AVPlayer? {
        guard let url = videoURL else {
            return nil
        }
        return AVPlayer(url: url)
    }

    func updateUIViewController(_ playerController: AVPlayerViewController, context: Context) {
        playerController.player = player
        playerController.player?.play()
    }

    func makeUIViewController(context: Context) -> AVPlayerViewController {
        AVPlayerViewController()
    }
}
