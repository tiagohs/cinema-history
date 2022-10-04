//
//  Int+Extensions.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import Foundation

extension Int {
    
    func toCurrency() -> String? {
        let price = self
        let currencyFormatter = NumberFormatter()
        
        currencyFormatter.usesGroupingSeparator = true
        currencyFormatter.numberStyle = .currency
        currencyFormatter.locale = Locale.current

        return currencyFormatter.string(from: NSNumber(value: price))
    }
}
