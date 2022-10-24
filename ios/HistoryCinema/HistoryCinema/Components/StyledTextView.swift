//
//  TextHtmlView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import WebKit
import SwiftUI

struct StyledTextView: View {
    let content: String
    var fontName: String? = nil
    var size: Int? = nil
    var color: String? = nil
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    
    @State var showLoading: Bool = true
    
    var body: some View {
        let fontN = fontName ?? "ProximaNova-Regular"
        let sizeFont = size ?? 14
        let colorFont = color ?? "#000"
        
        ZStack(alignment: .center) {
            if showLoading {
                LoadingView()
            }
            
            HTMLStringView(
                htmlContent: content,
                fontName: fontN,
                size: sizeFont,
                color: colorFont,
                onTextLoaded: {
                    showLoading = false
                },
                onClickLink: onClickLink)
        }
    }
}

struct HTMLStringView: UIViewRepresentable {
  let htmlContent: String
  var fontName: String
  var size: Int
  var color: String
  var lineSpace: Int? = nil
  var onTextLoaded: (() -> Void)? = nil
  var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    
  func makeUIView(context: Context) -> InteractiveLinkLabel {
    let label = InteractiveLinkLabel()
    
    label.onClickLink = onClickLink
    label.numberOfLines = 0
    label.lineBreakMode = .byTruncatingTail
    label.textAlignment = .justified
    label.isUserInteractionEnabled = true
    label.allowsDefaultTighteningForTruncation = true
    label.preferredMaxLayoutWidth = 0.9 * UIScreen.main.bounds.width
    
    // Compression resistance is important to enable auto resizing of this view,
    // that base on the SwiftUI layout.
    // Especially when the SwiftUI frame modifier applied to this view.
    label.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    label.setContentCompressionResistancePriority(.defaultLow, for: .vertical)
    
    // Maybe this is not necessary.
    label.clipsToBounds = true
    
    let labelTapGesture = UITapGestureRecognizer(target:self,action: #selector(MainViewHelper().closeAction))

    label.addGestureRecognizer(labelTapGesture)
    
    return label
  }

  func updateUIView(_ uiView: InteractiveLinkLabel, context: Context) {
      DispatchQueue.global(qos: .userInitiated).async {
          
          let text = htmlContent.htmlAttributedString(fontName: fontName, size: size, color: color)
              
        DispatchQueue.main.async {
            uiView.attributedText = text
            
            onTextLoaded?()
        }
          
      }
  }
}


class MainViewHelper {
  @objc func closeAction(sender: UITapGestureRecognizer) {
    print("tapped")
  }
}

struct StyledTextView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            StyledTextView(
                content: """
                Two roads diverged in a yellow wood,
                And sorry <b>I could not travel both</b>
                And be one <a http="https://google.com">traveler</a>, long I stood
                And looked down one as far as I could
                To where it bent in the undergrowth;
                <i>Two roads diverged in a wood, and I—
                I took the one less traveled by,
                And that has made all the difference.</i>
                """
            )
            Divider()
            Text("HTML Source Text:").font(.headline)
            Text(
                """
                Two roads diverged in a yellow wood,
                And sorry <b>I could not travel both</b>
                And be one traveler, long I stood
                And looked down one as far as I could
                To where it bent in the undergrowth;
                <i>Two roads diverged in a wood, and I—
                I took the one less traveled by,
                And that has made all the difference.</i>
                """
            )
            Spacer()
        }
    }
}
