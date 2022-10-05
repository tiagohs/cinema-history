//
//  PersonDetailsContentView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct PersonDetailsContentView: View {
    let person: Person!
    
    var body: some View {
        Text(person.name ?? "")
    }
}

struct PersonDetailsContentView_Previews: PreviewProvider {
    static var previews: some View {
        PersonDetailsContentView(person: Person.examplePersonFull)
    }
}
