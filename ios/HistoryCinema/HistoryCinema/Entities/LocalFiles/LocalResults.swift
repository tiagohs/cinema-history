//
//  Results.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper


class HomeContentResult: BaseLocalModel {
    var results: [HomeContentItem]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}

class SummaryResult: BaseLocalModel {
    var results: [Summary]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}

class NomineeResult: BaseLocalModel  {
    var results: [NomineeContent]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}

class HistoryResult: BaseLocalModel  {
    var results: [Content]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}

class NomineesResult: BaseLocalModel  {
    var results: [Nominee]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}

class ReferencesResult: BaseLocalModel  {
    var results: [ReferenceContent]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}

class GlossaryResult: BaseLocalModel  {
    var results: [Glossary]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}

class MainTopicsResult: BaseLocalModel  {
    var results: [MainTopic]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}
