//
//  DateFormatTransform.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

public class DateFormatTransform: TransformType {
    public typealias Object = Date
    public typealias JSON = Double
    
    var dateFormat = DateFormatter()
    
    convenience init(_ format: String) {
        self.init()
        self.dateFormat.dateFormat = format
    }
    
    open func transformFromJSON(_ value: Any?) -> Date? {
        if let timeInt = value as? Double {
            return Date(timeIntervalSince1970: TimeInterval(timeInt))
        }
        
        if let timeStr = value as? String {
            return self.dateFormat.date(from: timeStr)
        }
        
        return nil
    }
    
    open func transformToJSON(_ value: Date?) -> Double? {
        if let date = value {
            return Double(date.timeIntervalSince1970)
        }
        return nil
    }
}
