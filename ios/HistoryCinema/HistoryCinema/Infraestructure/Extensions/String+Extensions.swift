//
//  File.swift
//  popmovies
//
//  Created by Tiago Silva on 26/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import UIKit


extension String {
    
    func isValidEmail() -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        
        let emailPred = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailPred.evaluate(with: self)
    }
    
    func toJSONObject() throws -> Any {
        guard let file = self.toFileURL() else {
            throw "Could't find \(self) in main bundle"
        }
        
        let data = try (Data(contentsOf: file))
        
        return try JSONSerialization.jsonObject(with: data, options: [])
    }
    
    func toFileURL() -> URL? {
        return Bundle.main.url(forResource: self, withExtension: nil)
    }
}

extension String: Error {
    
}
