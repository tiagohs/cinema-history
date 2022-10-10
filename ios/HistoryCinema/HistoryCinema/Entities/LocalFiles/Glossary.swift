//
//  Glossary.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class Glossary: BaseLocalModel {
    var name: String!
    var contentList: [Content]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        name                                                    <- map["name"]
        contentList                                             <- map["content_list"]
    }
    
    static func exampleGlossary() -> Glossary {
        let contentList = [
            Content.exampleText,
            Content.exampleImage,
            Content.exampleVideo,
            Content.exampleRecomendation
        ]
        let glossary = Glossary(JSONString: "{\n          \"name\": \"Cinetoscópio\",\n          \"content_list\": [\n            {\n              \"type\": \"text\",\n              \"content_text\": \"O Cinetoscópio é um instrumento de projecção interna de filmes inventado por <strong>William Kennedy Laurie Dickson</strong>, chefe engenheiro da Edison Laboratories de Thomas Edison, em 1891.\"\n            },\n            {\n              \"type\": \"image\",\n              \"image\": {\n                \"image_type\": \"online\",\n                \"url\": \"https://i.pinimg.com/originals/85/74/5b/85745b4b4e07976ec94abc33735e4792.jpg\",\n                \"style\": {\n                  \"scale_type\": \"center_crop\"\n                }\n              },\n              \"information\": {\n                \"contentTitle\": \"Ilustração do interior de uma máquina de cinetoscópio.\",\n                \"source\": \"Le Kinétoscope d\'Edison\\\" by Gaston Tissandier in La Nature\"\n              }\n            },\n            {\n              \"type\": \"text\",\n              \"content_text\": \"Possuía um visor individual através do qual se podia assistir, mediante a inserção de uma moeda, à exibição de uma pequena tira de filme em Looping, na qual apareciam imagens em movimento de números cômicos, animais amestrados e bailarinas.\"\n            },\n            {\n              \"type\": \"text\",\n              \"content_text\": \"Os filmes reproduzidos no cinetoscópio (quinetoscópio) eram produzidos pelo cinetógrafo (quinetógrafo), outra invenção patenteada por Thomas Edison. Apesar destas invenções permanecerem no nome de Edison, elas, na verdade, foram produzidas por William K.L. Dickson e uma equipe de técnicos encarregados, por Edison, de criarem máquinas que produzissem e mostrassem fotografias em movimento ( motion picture). Em 1889, Edison decidiu criar a câmera descrita, quando viu a câmera de Étienne-Jules Marey, em Paris.\"\n            },\n            {\n              \"type\": \"video\",\n              \"video_id\": \"sfI0NVC0hLU\",\n              \"information\": {\n                \"contentTitle\": \"The Kinetoscope (O Cinetoscópio)\",\n                \"contentText\": \"O Cinetoscópio é um instrumento de projeção interna de filmes inventado por William Kennedy Laurie Dickson, chefe engenheiro da Edison Laboratories de Thomas Edison, em 1891.\",\n                \"source\": \"Youtube: Canal <i>AmericanExperiencePBS</i>\"\n              }\n            },\n            {\n              \"type\": \"recomendations\",\n              \"list\": [\n                {\n                  \"title\": \"O que é um Cinetoscópio?\",\n                  \"subtitle\": \"cinetoscópio.com\",\n                  \"link\": \"https://cinetoscopio.com.br/2017/12/22/o-que-e-um-cinetoscopio/\"\n                },\n                {\n                  \"title\": \"ASC Museum: Kinetoscope\",\n                  \"subtitle\": \"The American Society of Cinematographers\",\n                  \"link\": \"https://theasc.com/asc/asc-museum-kinetoscope\"\n                }\n              ]\n            }\n          ]\n        }")!
        
        glossary.contentList = contentList
        
        return glossary
    }
    
    
}
