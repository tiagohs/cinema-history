
import Foundation
import ObjectMapper

struct SpokenLanguage : BaseModel {
    var id: Int? = UUID().hashValue
	var country : String?
	var name : String?

    enum CodingKeys: String, CodingKey {
        case name
        case country = "iso_639_1"
	}

}
