//
//  Reviews.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class LocalReview: BaseLocalModel {
    var reviewerType: ReviewerType?
    var reviewerSiteName : String?
    var dateFormated : String?
    var reviewerName : String!
    var reviewUrl : String!
    var reviewDescription : String!
    var reviewRating : Float!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        reviewerType                                            <- (map["reviewer"], EnumTransform<ReviewerType>())
        reviewerSiteName                                        <- map["reviewer_site_name"]
        dateFormated                                            <- map["date_formated"]
        reviewerName                                            <- map["reviewer_name"]
        reviewUrl                                               <- map["review_url"]
        reviewDescription                                       <- map["review_description"]
        reviewRating                                            <- map["review_rating"]
    }
}

class ReviewsResult: BaseLocalModel {
    var languageISO : String?
    var reviews : [LocalReview]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        languageISO                                        <- map["language"]
        reviews                                            <- map["reviews"]
    }
}
