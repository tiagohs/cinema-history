
import Foundation

struct TaggedImages: BaseModel {
    var results : [TaggedImagesResults]?
    var page : Int?
    var total_results : Int?
    var id : Int?
    var total_pages : Int?
}
