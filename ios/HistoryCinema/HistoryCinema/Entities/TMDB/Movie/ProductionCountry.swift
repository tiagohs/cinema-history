
import Foundation

struct ProductionCountry : BaseModel {
    var id: Int? = UUID().hashValue
	var country : String?
	var name : String?
}
