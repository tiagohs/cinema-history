//
//  HomeButtonListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/09/22.
//

import SwiftUI

struct HomeButtonListView<
    ReferencesDestinationView : View,
    GlossaryDestinationView : View,
    AboutDestinationView : View
>: View {
    @ViewBuilder var ReferencesDestination: () -> ReferencesDestinationView
    @ViewBuilder var GlossaryDestination: () -> GlossaryDestinationView
    @ViewBuilder var AboutDestination: () -> AboutDestinationView
    
    @State private var selection: String? = nil
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                NavigationLink(destination: ReferencesDestination(), tag: "ReferencesDestination", selection: $selection) {
                    Button(action: {
                        self.selection = "ReferencesDestination"
                    }) {
                        HStack {
                            Text("Referências")
                                .font(.proximaNovaBold(size: 18))
                        }
                        .padding(20)
                        .foregroundColor(.cardTextPrimary)
                        .background(Color.cardBackground)
                        .cornerRadius(20)
                    }
                    .shadow(radius: 5)
                }
                
                NavigationLink(destination: GlossaryDestination(), tag: "GlossaryDestination", selection: $selection) {
                    Button(action: {
                        self.selection = "GlossaryDestination"
                    }) {
                        HStack {
                            Text("Glossário")
                                .font(.proximaNovaBold(size: 18))
                        }
                        .padding(20)
                        .foregroundColor(.cardTextPrimary)
                        .background(Color.cardBackground)
                        .cornerRadius(20)
                    }
                    .shadow(radius: 5)
                }
                
                NavigationLink(destination: AboutDestination(), tag: "AboutDestination", selection: $selection) {
                    Button(action: {
                        
                    }) {
                        HStack {
                            Text("Sobre")
                                .font(.proximaNovaBold(size: 18))
                        }
                        .padding(20)
                        .foregroundColor(.cardTextPrimary)
                        .background(Color.cardBackground)
                        .cornerRadius(20)
                    }
                    .shadow(radius: 5)
                }
            }
        }
        .padding()
    }
}

struct HomeButtonListView_Previews: PreviewProvider {
    static var previews: some View {
        HomeButtonListView(
            ReferencesDestination: { AnyView(EmptyView()) },
            GlossaryDestination: { AnyView(EmptyView()) },
            AboutDestination: { AnyView(EmptyView()) }
        )
    }
}
