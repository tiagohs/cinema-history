//
//  Reference.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class Reference: BaseLocalModel {
    var type: ReferenceType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        type                                                    <- (map["type"], EnumTransform<ReferenceType>())
    }
    
    static func getReference(from dictionary: Dictionary<String, Any>, by referenceType: ReferenceType) throws -> Reference? {
        switch referenceType {
        case .text:
            return ReferenceText(JSON: dictionary)
        case .media:
            return ReferenceMedia(JSON: dictionary)
        }
    }
    
    static func example(_ referenceType: ReferenceType) -> Reference {
        switch referenceType {
        case .text:
            return ReferenceText(JSONString: "{\n              \"type\": \"text\",\n              \"text\": \"As informações dos filmes, atores, diretores e as imagens são fornecidas pela <strong>API TMDb</strong> mas não é endossado ou certificado pelo TMDb. <br/><br/><a href=\\\"https://_{\'type\': \'online\', \'id\': 11523, \'url\': \'https://www.themoviedb.org/terms-of-use\'}\\\">Termos de Uso</a> \\n\\n<br/><a href=\\\"https://_{\'type\': \'online\', \'id\': 11523, \'url\': \'https://www.themoviedb.org/documentation/api/terms-of-use\'}\\\">Termos de Uso da API </a>\"\n            }")!
        case .media:
            return ReferenceMedia(JSONString: "{\n              \"type\": \"media\",\n              \"title\": \"O livro do cinema Livro de teste com titutlo\",\n              \"subtitle\": \"Danny Leigh, Louis Baxter, Kieran Gant, John Farndon, Damon Wise (Colaboradores)\",\n              \"description\": \"Será que Chinatown realmente subverteu o gênero noir? Quais os efeitos causados pelo roteiro fragmentado de Pulp Fiction? Como os múltiplos pontos de vista afetam a noção de realidade em Rashomon? Ao exibir os melhores filmes de todos os tempos, este livro responde a estas questões e tantas outras enquanto traz um panorama do mundo do cinema. Escrito em linguagem simples e recheado de imagens icônicas dos filmes, além de pôsteres e infográficos, O livro do cinema explora os personagens, diretores, roteiros e temaschave de mais de cem dos maiores filmes já feitos. Seja você um grande cinéfilo ou um frequentador ocasional das salas de cinema procurando indicações, este livro te dará novas perspectivas sobre seus filmes prediletos ou uma introdução a uma grande quantidade de outras joias que você não pode deixar de ver.\",\n              \"link\": \"https://www.amazon.com.br/Livro-do-Cinema-V%C3%A1rios-Autores/dp/8525062421/\",\n              \"mediaType\": \"Livro\",\n              \"image\": {\n                \"image_type\": \"online\",\n                \"url\": \"https://images-na.ssl-images-amazon.com/images/I/51HxDGyMZ3L._SX401_BO1,204,203,200_.jpg\",\n                \"content_description\": \"Capa do Livro O livro do cinema\"\n              }\n            }")!
        }
    }
}

class ReferenceContent: BaseLocalModel {
    var name: String!
    var references: [Reference]?

    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        name                                                    <- map["name"]
        references                                              <- map["references"]
    }
    
    static func exampleReference(_ references: [Reference]) -> ReferenceContent {
        let referenceContent = ReferenceContent(JSONString: "{\n          \"name\": \"Tudo\",\n          \"references\": []\n        }")!
        
        referenceContent.references = references
        
        return referenceContent
    }
    
    
}
