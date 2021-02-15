

struct ProductionCompany : BaseModel {
	var id : Int?
	var logoPath : String?
	var name : String?
	var originCountry : String?

    enum CodingKeys: String, CodingKey {
		case id, name
		case logoPath = "logo_path"
		case originCountry = "origin_country"
	}
}
