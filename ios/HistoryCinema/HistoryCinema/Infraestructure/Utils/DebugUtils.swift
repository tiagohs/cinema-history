//
//  DataRequest+Extensions.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import Alamofire

class DebugUtils {
    static func debug(_ response: AFDataResponse<Any>) {
        #if DEBUG
            print(response)
            print(response.request?.url)
        #endif
    }
}
