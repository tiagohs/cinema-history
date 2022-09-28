//
//  InteractiveLinkLabel.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import Foundation
import UIKit

class InteractiveLinkLabel: UILabel {
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
        self.configure()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.configure()
    }
    
    func configure() {
        isUserInteractionEnabled = true
    }
    
    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        
        let superBool = super.point(inside: point, with: event)
        
        // Configure NSTextContainer
        let textContainer = NSTextContainer(size: bounds.size)
        textContainer.lineFragmentPadding = 0.0
        textContainer.lineBreakMode = lineBreakMode
        textContainer.maximumNumberOfLines = numberOfLines
        
        // Configure NSLayoutManager and add the text container
        let layoutManager = NSLayoutManager()
        layoutManager.addTextContainer(textContainer)
        
        guard let attributedText = attributedText else {return false}
        
        // Configure NSTextStorage and apply the layout manager
        let textStorage = NSTextStorage(attributedString: attributedText)
        textStorage.addAttribute(NSAttributedString.Key.font, value: font!, range: NSMakeRange(0, attributedText.length))
        textStorage.addLayoutManager(layoutManager)
        
        // get the tapped character location
        let locationOfTouchInLabel = point
        
        // account for text alignment and insets
        let textBoundingBox = layoutManager.usedRect(for: textContainer)
        var alignmentOffset: CGFloat!
        switch textAlignment {
        case .left, .natural, .justified:
            alignmentOffset = 0.0
        case .center:
            alignmentOffset = 0.5
        case .right:
            alignmentOffset = 1.0
        @unknown default:
            fatalError()
        }
        
        let xOffset = ((bounds.size.width - textBoundingBox.size.width) * alignmentOffset) - textBoundingBox.origin.x
        let yOffset = ((bounds.size.height - textBoundingBox.size.height) * alignmentOffset) - textBoundingBox.origin.y
        let locationOfTouchInTextContainer = CGPoint(x: locationOfTouchInLabel.x - xOffset, y: locationOfTouchInLabel.y - yOffset)
        
        // work out which character was tapped
        let characterIndex = layoutManager.characterIndex(for: locationOfTouchInTextContainer, in: textContainer, fractionOfDistanceBetweenInsertionPoints: nil)
        
        // work out how many characters are in the string up to and including the line tapped, to ensure we are not off the end of the character string
        let lineTapped = Int(ceil(locationOfTouchInLabel.y / font.lineHeight)) - 1
        let rightMostPointInLineTapped = CGPoint(x: bounds.size.width, y: font.lineHeight * CGFloat(lineTapped))
        let charsInLineTapped = layoutManager.characterIndex(for: rightMostPointInLineTapped, in: textContainer, fractionOfDistanceBetweenInsertionPoints: nil)
        
        guard characterIndex < charsInLineTapped else {return false}
        
        let attributeName = NSAttributedString.Key.link
        
        let attributeValue = self.attributedText?.attribute(attributeName, at: characterIndex, effectiveRange: nil)
        
        if let value = attributeValue, let linkContent = value as? URL {
            self.openLink(linkContent.absoluteString)
        }
        
        return superBool
    }
    
    private func openLink(_ linkContent: String) {
        let value = linkContent
                        .replacingOccurrences(of: "%7B", with: "{", options: .literal, range: nil)
                        .replacingOccurrences(of: "%20", with: " ", options: .literal, range: nil)
                        .replacingOccurrences(of: "%7D", with: "}", options: .literal, range: nil)
                        .replacingOccurrences(of: "https://_", with: "", options: .literal, range: nil)
                        .replacingOccurrences(of: "'", with: "\"", options: .literal, range: nil)
                        .replacingOccurrences(of: "{type", with: "{\"type\"", options: .literal, range: nil)
                        .replacingOccurrences(of: "movie}", with: "\"movie\"}", options: .literal, range: nil)
                        .replacingOccurrences(of: "person}", with: "\"person\"}", options: .literal, range: nil)
    
        guard let data = value.data(using: .utf8) else {
            return
        }
        
        guard let dictionary = try? JSONSerialization.jsonObject(with: data, options: []) as? Dictionary<String, Any> else {
            return
        }
    
        guard let typeName = dictionary["type"] as? String else {
            return
        }
        
        let type = TextViewUrlType.getTextViewUrlType(by: typeName)
        
        switch type {
        case .screen:
            let textViewLinkScreen = TextViewLinkScreen(JSON: dictionary)
            
            onClickLink?(textViewLinkScreen)
        case .online:
            let textViewLinkOnline = TextViewLinkOnline(JSON: dictionary)
            
            guard let urlString = textViewLinkOnline?.url, let url = URL(string: urlString) else {
                return
            }
            
            UIApplication.shared.open(url)
        }
    }
}
