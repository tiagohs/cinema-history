//
//  Person.swift
//  popmovies
//
//  Created by Tiago Silva on 19/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct Person: BaseModel {
    var id : Int?
    var birthday : String?
    var knownForDepartment : String?
    var placeOfBirth : String?
    var homepage : String?
    var profilePath : String?
    var imdbId : String?
    var deathday : String?
    var name : String?
    var alsoKnownAs : [String]?
    var biography : String?
    var adult : Bool?
    var gender : Int?
    var popularity : Double?
    
    // Append To Reponse
    var images : Images?
    var externalIds : ExternalIds?
    var movieCredits : MovieCredits?
    var taggedImages : TaggedImages?
    var translations: TranslationPersonResults?
    
    //Custom
    var allMovieCredits : [Movie] = []
    
    enum CodingKeys: String, CodingKey {
        case id, birthday, homepage, deathday, images, name, biography, adult, gender, popularity, translations
        case knownForDepartment = "known_for_department"
        case placeOfBirth = "place_of_birth"
        case profilePath = "profile_path"
        case imdbId = "imdb_id"
        case externalIds = "external_ids"
        case alsoKnownAs = "also_known_as"
        case movieCredits = "movie_credits"
        case taggedImages = "tagged_images"
    }
    
    func birthdayFull() -> String? {

        if let birthdayDate = self.birthday, let placeOfBirth = self.placeOfBirth {
            if !birthdayDate.isEmpty && placeOfBirth.isEmpty {
                return birthdayDate.toDate()?.formatDate(pattner: "MMMM dd, yyyy")
            }
            
            if !placeOfBirth.isEmpty && birthdayDate.isEmpty {
                return placeOfBirth
            }
            
            if let birthdayDateFormat = birthdayDate.toDate()?.formatDate(pattner: "MMMM dd, yyyy") {
                return "\(birthdayDateFormat) in \(placeOfBirth)"
            }
        }
        
        return nil
    }
    
    func biographyFull() -> String? {
        let defaultBiography = self.biography
        
        if let translations = self.translations?.translations {
            if let portugueseBiography = translations.first(where: {  $0.iso_639_1 == "pt" && $0.iso_3166_1 == "BR" })?.data?.biography, !portugueseBiography.isEmpty {
                return portugueseBiography
            }
            
            if let englishBiography = translations.first(where: { $0.iso_639_1 == "en" && $0.iso_3166_1 == "US" })?.data?.biography, !englishBiography.isEmpty {
                return englishBiography
            }
        }
        
        return defaultBiography
    }
    
    func departments() -> [String] {
        var departmentList: [String] = []

        if let cast = movieCredits?.cast, !cast.isEmpty  {
            departmentList.append("Acting")
        }

        if let crew = movieCredits?.crew {
            crew.forEach { crewItem in
                if let department = crewItem.department, !departmentList.contains(where: { departmentSaved in
                    departmentSaved == department
                }) {
                    departmentList.append(department)
                }
            }
            
            return departmentList
        }
        
        return departmentList
    }
    
    func externalLinks() -> [ExternalLinkDTO] {
        var externalLinks: [ExternalLinkDTO] = []
        
        if let facebookId = self.externalIds?.facebookId, !facebookId.isEmpty {
            externalLinks.append(ExternalLinkDTO(image: "ic_facebook_grey", link: "https://www.facebook.com/\(facebookId)"))
        }
        
        if let instagramId = self.externalIds?.instagramId, !instagramId.isEmpty {
            externalLinks.append(ExternalLinkDTO(image: "ic_instagram_grey", link: "https://instagram.com/\(instagramId)"))
        }
        
        if let twitterId = self.externalIds?.twitterId, !twitterId.isEmpty {
            externalLinks.append(ExternalLinkDTO(image: "ic_twitter_grey", link: "https://twitter.com/\(twitterId)"))
        }
        
        if let homepage = self.homepage, !homepage.isEmpty {
            externalLinks.append(ExternalLinkDTO(image: "ic_link_grey", link: homepage))
        }
        
        return externalLinks
    }
    
    func filmography() -> [MovieDTO] {
        let castMovies = self.movieCredits?.cast?.map({ creditCast in
            return MovieDTO(
                id: creditCast.id,
                posterPath: creditCast.posterPath,
                title: creditCast.originalTitle,
                subtitle: creditCast.character,
                year: creditCast.releaseDate?.toDate()?.yearInt
            )
        }) ?? []
        let crewMovies = self.movieCredits?.crew?.map({ creditCrew in
            return MovieDTO(
                id: creditCrew.id,
                posterPath: creditCrew.posterPath,
                title: creditCrew.originalTitle,
                subtitle: creditCrew.department,
                year: creditCrew.releaseDate?.toDate()?.yearInt
            )
        }) ?? []
        let allMovies = castMovies + crewMovies
        
        return allMovies.sorted(by: { movie1, movie2 in movie1.year ?? 0 > movie2.year ?? 0 })
    }
    
    func allImages() -> [APIImage] {
        let profiles = self.images?.profiles ?? []
        let taggedImages = self.taggedImages?.results ?? []
        
        return taggedImages.map({ taggedImagesResult in
            return APIImage(
                id: taggedImagesResult.id,
                filePath: taggedImagesResult.filePath
            )
        }) + profiles
    }
    
    static let examplePersonFull = try! JSONDecoder().decode(Person.self, from: "{\n   \"adult\":false,\n   \"also_known_as\":[\n      \"브래들리 쿠퍼\",\n      \"布莱恩·科兰斯顿\",\n      \"Брэдли Купер\",\n      \"برادلي كوبر\",\n      \"ブラッドリー・クーパー\",\n      \"แบรดลีย์ คูเปอร์\",\n      \"布萊德利·古柏\",\n      \"Bradley Charles Cooper\",\n      \"Μπράντλεϊ Τσαρλς Κούπερ\",\n      \"Μπράντλεϊ Κούπερ\"\n   ],\n   \"biography\":\"Bradley Charles Cooper (born January 5, 1975) is an American actor and filmmaker. He is the recipient of various accolades, including a British Academy Film Award and two Grammy Awards, in addition to nominations for nine Academy Awards, six Golden Globe Awards, and a Tony Award. Cooper appeared on the Forbes Celebrity 100 three times and on Time\'s list of the 100 most influential people in the world in 2015. His films have grossed $11 billion worldwide and he has placed four times in annual rankings of the world\'s highest-paid actors.\\n\\nCooper enrolled in the MFA program at the Actors Studio in 2000 after beginning his career in 1999 with a guest role in the television series Sex and the City. He made his film debut in the comedy Wet Hot American Summer (2001). He first gained recognition as Will Tippin in the spy-action television show Alias (2001–2006), and achieved minor success with a supporting part in the comedy film Wedding Crashers (2005). His breakthrough role came in 2009 with The Hangover, a critically and commercially successful comedy, which spawned two sequels in 2011 and 2013. Cooper\'s portrayal of a struggling writer in the thriller Limitless (2011) and a rookie police officer in the crime drama The Place Beyond the Pines (2012) drew praise from critics.\\n\\nCooper found greater success with the romantic comedy Silver Linings Playbook (2012), the black comedy American Hustle (2013), and the war biopic American Sniper (2014), which he also produced. For his work in these films, he was nominated for four Academy Awards, becoming the tenth actor to receive an Oscar nomination in three consecutive years. In 2014, he portrayed Joseph Merrick in a Broadway revival of The Elephant Man, garnering a nomination for the Tony Award for Best Actor in a Play, and began voicing Rocket Raccoon in the Marvel Cinematic Universe. In 2018, Cooper produced, wrote, directed and starred in a remake of the musical romance A Star Is Born. He earned three Oscar nominations for the film, as well as a BAFTA Award and two Grammys for his contributions to its U.S. Billboard 200 number one soundtrack and its chart-topping lead single \\\"Shallow\\\". He gained Academy Award nominations for producing Joker (2019) and Nightmare Alley (2021).\\n\\nLabeled a sex symbol by the media, Cooper was named People magazine\'s \\\"Sexiest Man Alive\\\" in 2011. He supports several charities that help fight cancer. Cooper was briefly married to actress Jennifer Esposito, and has a daughter from his relationship with model Irina Shayk.\",\n   \"birthday\":\"1975-01-05\",\n   \"deathday\":null,\n   \"gender\":2,\n   \"homepage\":null,\n   \"id\":51329,\n   \"imdb_id\":\"nm0177896\",\n   \"known_for_department\":\"Acting\",\n   \"name\":\"Bradley Cooper\",\n   \"place_of_birth\":\"Philadelphia, Pennsylvania, USA\",\n   \"popularity\":42.826,\n   \"profile_path\":\"/pLD2XvxqHueLWOuqXoFngJU3A5H.jpg\",\n   \"tagged_images\":{\n      \"page\":1,\n      \"results\":[\n         {\n            \"aspect_ratio\":0.6666666666666666,\n            \"file_path\":\"/wrFpXMNBRj2PBiN4Z5kix51XaIZ.jpg\",\n            \"height\":3000,\n            \"id\":\"5b8c0edcc3a3685209036ee1\",\n            \"iso_639_1\":\"en\",\n            \"vote_average\":6.222,\n            \"vote_count\":26,\n            \"width\":2000,\n            \"image_type\":\"poster\",\n            \"media\":{\n               \"adult\":false,\n               \"backdrop_path\":\"/dDYpjrwh1wNVQk0rEpc9P81wQt4.jpg\",\n               \"id\":332562,\n               \"title\":\"A Star Is Born\",\n               \"original_language\":\"en\",\n               \"original_title\":\"A Star Is Born\",\n               \"overview\":\"Seasoned musician Jackson Maine discovers — and falls in love with — struggling artist Ally. She has just about given up on her dream to make it big as a singer — until Jack coaxes her into the spotlight. But even as Ally\'s career takes off, the personal side of their relationship is breaking down, as Jack fights an ongoing battle with his own internal demons.\",\n               \"poster_path\":\"/wrFpXMNBRj2PBiN4Z5kix51XaIZ.jpg\",\n               \"media_type\":\"movie\",\n               \"genre_ids\":[\n                  18,\n                  10749,\n                  10402\n               ],\n               \"popularity\":36.684,\n               \"release_date\":\"2018-10-03\",\n               \"video\":false,\n               \"vote_average\":7.514,\n               \"vote_count\":10463\n            },\n            \"media_type\":\"movie\"\n         },\n         {\n            \"aspect_ratio\":0.6666666666666666,\n            \"file_path\":\"/i1U46OwMc6vlm7OoSUKfqUH615e.jpg\",\n            \"height\":3000,\n            \"id\":\"5a420695c3a368586c03296e\",\n            \"iso_639_1\":\"en\",\n            \"vote_average\":5.522,\n            \"vote_count\":4,\n            \"width\":2000,\n            \"image_type\":\"poster\",\n            \"media\":{\n               \"adult\":false,\n               \"backdrop_path\":\"/smAnf46gZL1jXuP9nZvkM3Uynav.jpg\",\n               \"id\":190859,\n               \"title\":\"American Sniper\",\n               \"original_language\":\"en\",\n               \"original_title\":\"American Sniper\",\n               \"overview\":\"U.S. Navy SEAL Chris Kyle takes his sole mission—protect his comrades—to heart and becomes one of the most lethal snipers in American history. His pinpoint accuracy not only saves countless lives but also makes him a prime target of insurgents. Despite grave danger and his struggle to be a good husband and father to his family back in the States, Kyle serves four tours of duty in Iraq. However, when he finally returns home, he finds that he cannot leave the war behind.\",\n               \"poster_path\":\"/i1U46OwMc6vlm7OoSUKfqUH615e.jpg\",\n               \"media_type\":\"movie\",\n               \"genre_ids\":[\n                  10752,\n                  28\n               ],\n               \"popularity\":95.979,\n               \"release_date\":\"2014-12-25\",\n               \"video\":false,\n               \"vote_average\":7.437,\n               \"vote_count\":11388\n            },\n            \"media_type\":\"movie\"\n         },\n         {\n            \"aspect_ratio\":1.777777777777778,\n            \"file_path\":\"/yHnypVzF5jFdDwBmKiB2FTjiPR8.jpg\",\n            \"height\":720,\n            \"id\":\"53b80b930e0a2676c4007753\",\n            \"iso_639_1\":null,\n            \"vote_average\":5.3125,\n            \"vote_count\":1,\n            \"width\":1280,\n            \"image_type\":\"still\",\n            \"media\":{\n               \"id\":993961,\n               \"name\":\"Bradley Cooper, Tim McGraw\",\n               \"overview\":\"Actor Bradley Cooper; Tim McGraw performs.\",\n               \"media_type\":\"tv_episode\",\n               \"vote_average\":0.0,\n               \"vote_count\":0,\n               \"air_date\":\"2014-02-19\",\n               \"episode_number\":3,\n               \"production_code\":\"\",\n               \"runtime\":45,\n               \"season_number\":1,\n               \"show_id\":59941,\n               \"still_path\":\"/yHnypVzF5jFdDwBmKiB2FTjiPR8.jpg\"\n            },\n            \"media_type\":\"episode\"\n         },\n         {\n            \"aspect_ratio\":0.6666666666666666,\n            \"file_path\":\"/buz506hPSQVR26ZpcHjuA830iAS.jpg\",\n            \"height\":1656,\n            \"id\":\"5a0a70b892514130b200fb23\",\n            \"iso_639_1\":\"en\",\n            \"vote_average\":5.27,\n            \"vote_count\":10,\n            \"width\":1104,\n            \"image_type\":\"poster\",\n            \"media\":{\n               \"adult\":false,\n               \"backdrop_path\":\"/dDYpjrwh1wNVQk0rEpc9P81wQt4.jpg\",\n               \"id\":332562,\n               \"title\":\"A Star Is Born\",\n               \"original_language\":\"en\",\n               \"original_title\":\"A Star Is Born\",\n               \"overview\":\"Seasoned musician Jackson Maine discovers — and falls in love with — struggling artist Ally. She has just about given up on her dream to make it big as a singer — until Jack coaxes her into the spotlight. But even as Ally\'s career takes off, the personal side of their relationship is breaking down, as Jack fights an ongoing battle with his own internal demons.\",\n               \"poster_path\":\"/wrFpXMNBRj2PBiN4Z5kix51XaIZ.jpg\",\n               \"media_type\":\"movie\",\n               \"genre_ids\":[\n                  18,\n                  10749,\n                  10402\n               ],\n               \"popularity\":36.684,\n               \"release_date\":\"2018-10-03\",\n               \"video\":false,\n               \"vote_average\":7.514,\n               \"vote_count\":10463\n            },\n            \"media_type\":\"movie\"\n         },\n         {\n            \"aspect_ratio\":1.777251184834123,\n            \"file_path\":\"/vd7s19EkPHBXHmZ1cWwi8awyLgM.jpg\",\n            \"height\":1688,\n            \"id\":\"53b82fbc0e0a2676bb007baa\",\n            \"iso_639_1\":null,\n            \"vote_average\":5.238095238095238,\n            \"vote_count\":1,\n            \"width\":3000,\n            \"image_type\":\"still\",\n            \"media\":{\n               \"id\":993961,\n               \"name\":\"Bradley Cooper, Tim McGraw\",\n               \"overview\":\"Actor Bradley Cooper; Tim McGraw performs.\",\n               \"media_type\":\"tv_episode\",\n               \"vote_average\":0.0,\n               \"vote_count\":0,\n               \"air_date\":\"2014-02-19\",\n               \"episode_number\":3,\n               \"production_code\":\"\",\n               \"runtime\":45,\n               \"season_number\":1,\n               \"show_id\":59941,\n               \"still_path\":\"/yHnypVzF5jFdDwBmKiB2FTjiPR8.jpg\"\n            },\n            \"media_type\":\"episode\"\n         },\n         {\n            \"aspect_ratio\":1.777777777777778,\n            \"file_path\":\"/eAHluoOdq3NZXDm0Vne2oXpLCk8.jpg\",\n            \"height\":450,\n            \"id\":\"55fd5a8392514152b1000bb3\",\n            \"iso_639_1\":null,\n            \"vote_average\":0.0,\n            \"vote_count\":0,\n            \"width\":800,\n            \"image_type\":\"still\",\n            \"media\":{\n               \"id\":1091579,\n               \"name\":\"Pilot\",\n               \"overview\":\"Brian Finch\'s life takes an extraordinary turn when he uses a mysterious drug, NZT, that allows him to access his full brain capacity. However, when an NZT-related murder lands Brian on the FBI\'s radar, he uses the drug in an attempt to stay ahead of the authorities long enough to clear his name.\",\n               \"media_type\":\"tv_episode\",\n               \"vote_average\":7.439,\n               \"vote_count\":33,\n               \"air_date\":\"2015-09-22\",\n               \"episode_number\":1,\n               \"production_code\":\"LIM101\",\n               \"runtime\":45,\n               \"season_number\":1,\n               \"show_id\":62687,\n               \"still_path\":\"/bg2QIwX0nwIdrFihSeuxKEeIU0v.jpg\"\n            },\n            \"media_type\":\"episode\"\n         }\n      ],\n      \"total_pages\":1,\n      \"total_results\":6\n   },\n   \"movie_credits\":{\n      \"cast\":[\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/rlFaYFHwUmetf6VhKDIwrYEOeez.jpg\",\n            \"genre_ids\":[\n               27,\n               9648,\n               14\n            ],\n            \"id\":10185,\n            \"original_language\":\"en\",\n            \"original_title\":\"The Midnight Meat Train\",\n            \"overview\":\"A photographer\'s obsessive pursuit of dark subject matter leads him into the path of a serial killer who stalks late night commuters, ultimately butchering them in the most gruesome ways.\",\n            \"popularity\":17.039,\n            \"poster_path\":\"/x5YbfyUA8huK0shr008xwzNVJ5Y.jpg\",\n            \"release_date\":\"2008-08-07\",\n            \"title\":\"The Midnight Meat Train\",\n            \"video\":false,\n            \"vote_average\":6.085,\n            \"vote_count\":856,\n            \"character\":\"Leon Kaufman\",\n            \"credit_id\":\"52fe433d9251416c75008c8d\",\n            \"order\":0\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/zzcBf3kVmhJTANaFCJXiltsy64o.jpg\",\n            \"genre_ids\":[\n               35\n            ],\n            \"id\":45243,\n            \"original_language\":\"en\",\n            \"original_title\":\"The Hangover Part II\",\n            \"overview\":\"The Hangover crew heads to Thailand for Stu\'s wedding. After the disaster of a bachelor party in Las Vegas last year, Stu is playing it safe with a mellow pre-wedding brunch. However, nothing goes as planned and Bangkok is the perfect setting for another adventure with the rowdy group.\",\n            \"popularity\":105.593,\n            \"poster_path\":\"/7sGkjqorTHkaHTz8Q4WWHj8JL9t.jpg\",\n            \"release_date\":\"2011-05-25\",\n            \"title\":\"The Hangover Part II\",\n            \"video\":false,\n            \"vote_average\":6.45,\n            \"vote_count\":9157,\n            \"character\":\"Phil Wenneck\",\n            \"credit_id\":\"52fe46b8c3a36847f810e649\",\n            \"order\":0\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/2o0PKGmnSgCGkzoSePNAqse8Ure.jpg\",\n            \"genre_ids\":[\n               35\n            ],\n            \"id\":18785,\n            \"original_language\":\"en\",\n            \"original_title\":\"The Hangover\",\n            \"overview\":\"When three friends finally come to after a raucous night of bachelor-party revelry, they find a baby in the closet and a tiger in the bathroom. But they can\'t seem to locate their best friend, Doug – who\'s supposed to be tying the knot. Launching a frantic search for Doug, the trio perseveres through a nasty hangover to try to make it to the church on time.\",\n            \"popularity\":120.0,\n            \"poster_path\":\"/uluhlXubGu1VxU63X9VHCLWDAYP.jpg\",\n            \"release_date\":\"2009-06-02\",\n            \"title\":\"The Hangover\",\n            \"video\":false,\n            \"vote_average\":7.304,\n            \"vote_count\":14833,\n            \"character\":\"Phil Wenneck\",\n            \"credit_id\":\"52fe479a9251416c7509f5eb\",\n            \"order\":0\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/vQGo5VjJcHxpzIa8lMBFzpAth1w.jpg\",\n            \"genre_ids\":[\n               53,\n               9648,\n               878\n            ],\n            \"id\":51876,\n            \"original_language\":\"en\",\n            \"original_title\":\"Limitless\",\n            \"overview\":\"A paranoia-fueled action thriller about an unsuccessful writer whose life is transformed by a top-secret \\\"smart drug\\\" that allows him to use 100% of his brain and become a perfect version of himself. His enhanced abilities soon attract shadowy forces that threaten his new life in this darkly comic and provocative film.\",\n            \"popularity\":58.364,\n            \"poster_path\":\"/hv5JMCrMVLvV6HKPf9FcBuyk2MG.jpg\",\n            \"release_date\":\"2011-03-08\",\n            \"title\":\"Limitless\",\n            \"video\":false,\n            \"vote_average\":7.171,\n            \"vote_count\":9243,\n            \"character\":\"Eddie Morra\",\n            \"credit_id\":\"52fe4809c3a36847f81552c9\",\n            \"order\":0\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/b0GGZbLB7P9r4klx1wUcz77WzE6.jpg\",\n            \"genre_ids\":[\n               18,\n               53\n            ],\n            \"id\":83686,\n            \"original_language\":\"en\",\n            \"original_title\":\"The Words\",\n            \"overview\":\"The Words follows young writer Rory Jansen who finally achieves long sought after literary success after publishing the next great American novel. There\'s only one catch - he didn\'t write it. As the past comes back to haunt him and his literary star continues to rise, Jansen is forced to confront the steep price that must be paid for stealing another man\'s work, and for placing ambition and success above life\'s most fundamental three words.\",\n            \"popularity\":8.969,\n            \"poster_path\":\"/aJtPLZS7tlpiQl6pEr8H42K565x.jpg\",\n            \"release_date\":\"2012-09-07\",\n            \"title\":\"The Words\",\n            \"video\":false,\n            \"vote_average\":6.631,\n            \"vote_count\":983,\n            \"character\":\"Rory Jansen\",\n            \"credit_id\":\"52fe48b49251416c91095939\",\n            \"order\":0\n         }\n      ],\n      \"crew\":[\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/dDYpjrwh1wNVQk0rEpc9P81wQt4.jpg\",\n            \"genre_ids\":[\n               18,\n               10749,\n               10402\n            ],\n            \"id\":332562,\n            \"original_language\":\"en\",\n            \"original_title\":\"A Star Is Born\",\n            \"overview\":\"Seasoned musician Jackson Maine discovers — and falls in love with — struggling artist Ally. She has just about given up on her dream to make it big as a singer — until Jack coaxes her into the spotlight. But even as Ally\'s career takes off, the personal side of their relationship is breaking down, as Jack fights an ongoing battle with his own internal demons.\",\n            \"popularity\":35.165,\n            \"poster_path\":\"/wrFpXMNBRj2PBiN4Z5kix51XaIZ.jpg\",\n            \"release_date\":\"2018-10-03\",\n            \"title\":\"A Star Is Born\",\n            \"video\":false,\n            \"vote_average\":7.514,\n            \"vote_count\":10463,\n            \"credit_id\":\"5eece48059e8a90036c72e4a\",\n            \"department\":\"Crew\",\n            \"job\":\"Additional Music\"\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/qnYwh0tGs0wvjkf7pGPr3TmVFh6.jpg\",\n            \"genre_ids\":[\n               18,\n               80\n            ],\n            \"id\":97367,\n            \"original_language\":\"en\",\n            \"original_title\":\"The Place Beyond the Pines\",\n            \"overview\":\"A motorcycle stunt rider considers committing a crime in order to provide for his wife and child, an act that puts him on a collision course with a cop-turned-politician.\",\n            \"popularity\":17.292,\n            \"poster_path\":\"/d5YPKAHJYmfGBaDMGV6xE8wfsoa.jpg\",\n            \"release_date\":\"2013-03-14\",\n            \"title\":\"The Place Beyond the Pines\",\n            \"video\":false,\n            \"vote_average\":6.95,\n            \"vote_count\":4227,\n            \"credit_id\":\"5fe3de30b2e074003e74884c\",\n            \"department\":\"Crew\",\n            \"job\":\"Stunts\"\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/dDYpjrwh1wNVQk0rEpc9P81wQt4.jpg\",\n            \"genre_ids\":[\n               18,\n               10749,\n               10402\n            ],\n            \"id\":332562,\n            \"original_language\":\"en\",\n            \"original_title\":\"A Star Is Born\",\n            \"overview\":\"Seasoned musician Jackson Maine discovers — and falls in love with — struggling artist Ally. She has just about given up on her dream to make it big as a singer — until Jack coaxes her into the spotlight. But even as Ally\'s career takes off, the personal side of their relationship is breaking down, as Jack fights an ongoing battle with his own internal demons.\",\n            \"popularity\":35.165,\n            \"poster_path\":\"/wrFpXMNBRj2PBiN4Z5kix51XaIZ.jpg\",\n            \"release_date\":\"2018-10-03\",\n            \"title\":\"A Star Is Born\",\n            \"video\":false,\n            \"vote_average\":7.514,\n            \"vote_count\":10463,\n            \"credit_id\":\"57ea3a02c3a3687edc00080a\",\n            \"department\":\"Directing\",\n            \"job\":\"Director\"\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/1niOCiItNqpBIOVD5gbYCEYcM8J.jpg\",\n            \"genre_ids\":[\n               18,\n               10402\n            ],\n            \"id\":523607,\n            \"original_language\":\"en\",\n            \"original_title\":\"Maestro\",\n            \"overview\":\"A portrait of Leonard Bernstein\'s singular charisma and passion for music as he rose to fame as America\'s first native born, world-renowned conductor, all along following his ambition to compose both symphonic and popular Broadway works.\",\n            \"popularity\":1.4,\n            \"poster_path\":\"/mzgJPhHSkibRF0uOgzgqgOfQ5Jo.jpg\",\n            \"release_date\":\"\",\n            \"title\":\"Maestro\",\n            \"video\":false,\n            \"vote_average\":0.0,\n            \"vote_count\":0,\n            \"credit_id\":\"5af4be060e0a265e5f0020b1\",\n            \"department\":\"Directing\",\n            \"job\":\"Director\"\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/vQGo5VjJcHxpzIa8lMBFzpAth1w.jpg\",\n            \"genre_ids\":[\n               53,\n               9648,\n               878\n            ],\n            \"id\":51876,\n            \"original_language\":\"en\",\n            \"original_title\":\"Limitless\",\n            \"overview\":\"A paranoia-fueled action thriller about an unsuccessful writer whose life is transformed by a top-secret \\\"smart drug\\\" that allows him to use 100% of his brain and become a perfect version of himself. His enhanced abilities soon attract shadowy forces that threaten his new life in this darkly comic and provocative film.\",\n            \"popularity\":58.364,\n            \"poster_path\":\"/hv5JMCrMVLvV6HKPf9FcBuyk2MG.jpg\",\n            \"release_date\":\"2011-03-08\",\n            \"title\":\"Limitless\",\n            \"video\":false,\n            \"vote_average\":7.171,\n            \"vote_count\":9243,\n            \"credit_id\":\"52fe4809c3a36847f81552eb\",\n            \"department\":\"Production\",\n            \"job\":\"Executive Producer\"\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/t5cuFKDgf5hRWYpRZ3jAANu5kPr.jpg\",\n            \"genre_ids\":[\n               18,\n               35,\n               10749\n            ],\n            \"id\":82693,\n            \"original_language\":\"en\",\n            \"original_title\":\"Silver Linings Playbook\",\n            \"overview\":\"After spending eight months in a mental institution, a former teacher moves back in with his parents and tries to reconcile with his ex-wife.\",\n            \"popularity\":30.717,\n            \"poster_path\":\"/y7iOVneBvITlBdhy6tVqXVOa1Js.jpg\",\n            \"release_date\":\"2012-11-16\",\n            \"title\":\"Silver Linings Playbook\",\n            \"video\":false,\n            \"vote_average\":7.126,\n            \"vote_count\":10721,\n            \"credit_id\":\"52fe486b9251416c9108be25\",\n            \"department\":\"Production\",\n            \"job\":\"Executive Producer\"\n         }\n      ]\n   },\n   \"images\":{\n      \"profiles\":[\n         {\n            \"aspect_ratio\":0.667,\n            \"height\":3000,\n            \"iso_639_1\":null,\n            \"file_path\":\"/pLD2XvxqHueLWOuqXoFngJU3A5H.jpg\",\n            \"vote_average\":5.342,\n            \"vote_count\":15,\n            \"width\":2000\n         },\n         {\n            \"aspect_ratio\":0.667,\n            \"height\":1500,\n            \"iso_639_1\":null,\n            \"file_path\":\"/DPnessSsWqVXRbKm93PtMjB4Us.jpg\",\n            \"vote_average\":5.276,\n            \"vote_count\":12,\n            \"width\":1000\n         },\n         {\n            \"aspect_ratio\":0.667,\n            \"height\":3000,\n            \"iso_639_1\":null,\n            \"file_path\":\"/rVyVoBJl8WBM5kq6gFobV3Fgcay.jpg\",\n            \"vote_average\":5.118,\n            \"vote_count\":4,\n            \"width\":2000\n         }\n      ]\n   },\n   \"external_ids\":{\n      \"freebase_mid\":\"/m/01_xtx\",\n      \"freebase_id\":\"/en/bradley_cooper\",\n      \"imdb_id\":\"nm0177896\",\n      \"tvrage_id\":8294,\n      \"facebook_id\":null,\n      \"instagram_id\":null,\n      \"twitter_id\":null\n   },\n   \"translations\":{\n      \"translations\":[\n         {\n            \"iso_3166_1\":\"US\",\n            \"iso_639_1\":\"en\",\n            \"name\":\"English\",\n            \"english_name\":\"English\",\n            \"data\":{\n               \"biography\":\"Bradley Charles Cooper (born January 5, 1975) is an American actor and filmmaker. He is the recipient of various accolades, including a British Academy Film Award and two Grammy Awards, in addition to nominations for nine Academy Awards, six Golden Globe Awards, and a Tony Award. Cooper appeared on the Forbes Celebrity 100 three times and on Time\'s list of the 100 most influential people in the world in 2015. His films have grossed $11 billion worldwide and he has placed four times in annual rankings of the world\'s highest-paid actors.\\n\\nCooper enrolled in the MFA program at the Actors Studio in 2000 after beginning his career in 1999 with a guest role in the television series Sex and the City. He made his film debut in the comedy Wet Hot American Summer (2001). He first gained recognition as Will Tippin in the spy-action television show Alias (2001–2006), and achieved minor success with a supporting part in the comedy film Wedding Crashers (2005). His breakthrough role came in 2009 with The Hangover, a critically and commercially successful comedy, which spawned two sequels in 2011 and 2013. Cooper\'s portrayal of a struggling writer in the thriller Limitless (2011) and a rookie police officer in the crime drama The Place Beyond the Pines (2012) drew praise from critics.\\n\\nCooper found greater success with the romantic comedy Silver Linings Playbook (2012), the black comedy American Hustle (2013), and the war biopic American Sniper (2014), which he also produced. For his work in these films, he was nominated for four Academy Awards, becoming the tenth actor to receive an Oscar nomination in three consecutive years. In 2014, he portrayed Joseph Merrick in a Broadway revival of The Elephant Man, garnering a nomination for the Tony Award for Best Actor in a Play, and began voicing Rocket Raccoon in the Marvel Cinematic Universe. In 2018, Cooper produced, wrote, directed and starred in a remake of the musical romance A Star Is Born. He earned three Oscar nominations for the film, as well as a BAFTA Award and two Grammys for his contributions to its U.S. Billboard 200 number one soundtrack and its chart-topping lead single \\\"Shallow\\\". He gained Academy Award nominations for producing Joker (2019) and Nightmare Alley (2021).\\n\\nLabeled a sex symbol by the media, Cooper was named People magazine\'s \\\"Sexiest Man Alive\\\" in 2011. He supports several charities that help fight cancer. Cooper was briefly married to actress Jennifer Esposito, and has a daughter from his relationship with model Irina Shayk.\"\n            }\n         },\n         {\n            \"iso_3166_1\":\"BR\",\n            \"iso_639_1\":\"pt\",\n            \"name\":\"Português\",\n            \"english_name\":\"Portuguese\",\n            \"data\":{\n               \"biography\":\"Bradley Charles Cooper é um ator, cineasta e produtor americano. Começou sua carreira como convidado na série de televisão Sex and the City em 1999.\"\n            }\n         }\n      ]\n   }\n}".data(using: .utf8)!)
    
//    mutating func mergeMovies() -> [Movie] {
//        let castCredits = self.movieCredits?.cast?.map({ (creditCast) -> Movie in
//            let movie = Movie(id: creditCast.id, title: creditCast.title, posterPath: creditCast.posterPath, backdropPath: creditCast.backdropPath, releaseDate: creditCast.releaseDate)
//            
//            return movie
//        }) ?? []
//        let crewCredits = self.movieCredits?.crew?.map({ (creditCrew) -> Movie in
//            let movie = Movie()
//            
//            movie.id = creditCrew.id
//            movie.title = creditCrew.title
//            movie.posterPath = creditCrew.posterPath
//            movie.backdropPath = creditCrew.backdropPath
//            movie.releaseDate = creditCrew.releaseDate
//            
//            return movie
//        }) ?? []
//        
//        return Array(Set(castCredits + crewCredits))
//    }
    
    func mergeImages() -> [APIImage] {
        let taggedImages = self.taggedImages?.results?.map({ (taggedImagesResults) -> APIImage in
            return APIImage(filePath: taggedImagesResults.filePath)
        }) ?? []
        let profileImages = images?.profiles ?? []
        
        return Array(Set(taggedImages + profileImages))
    }
}
