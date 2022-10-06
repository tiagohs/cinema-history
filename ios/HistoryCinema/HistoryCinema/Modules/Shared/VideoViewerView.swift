//
//  VideoViewerView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 30/09/22.
//

import SwiftUI

struct VideoViewerView: View {
    let youtubeVideoId: String?
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            VStack {
                VideoPlayerView(youtubeVideoId: youtubeVideoId, autoplay: true)
                    .frame(height: 300, alignment: .center)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.black)
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "xmark")
                    .font(.system(size: 16, weight: .bold))
                    .padding()
                    .background(Color.backgroundPrimary)
                    .foregroundColor(Color.textPrimary)
                    .clipShape(Circle())
            }
            .shadow(color: Color.backgroundPrimary, radius: 5)
            .padding()
        }
    }
}

struct VideoViewerView_Previews: PreviewProvider {
    static var previews: some View {
        VideoViewerView(youtubeVideoId: "sfI0NVC0hLU")
    }
}
