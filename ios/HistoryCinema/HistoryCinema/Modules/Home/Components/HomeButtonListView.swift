//
//  HomeButtonListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/09/22.
//

import SwiftUI

struct HomeButtonListView<Content : View>: View {
    @ViewBuilder var ReferencesDestination: () -> Content
    @ViewBuilder var GlossaryDestination: () -> Content
    @ViewBuilder var SettingsDestination: () -> Content
    @ViewBuilder var AboutDestination: () -> Content
    
    @State private var selection: String? = nil
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                NavigationLink(destination: ReferencesDestination()) {
                    Button(action: {
                        
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
                }
                
                NavigationLink(destination: GlossaryDestination()) {
                    Button(action: {
                        
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
                }
                
                
            }
            
            HStack {
                NavigationLink(destination: SettingsDestination()) {
                    Button(action: {
                        
                    }) {
                        HStack {
                            Text("Configurações")
                                .font(.proximaNovaBold(size: 18))
                        }
                        .padding(20)
                        .foregroundColor(.cardTextPrimary)
                        .background(Color.cardBackground)
                        .cornerRadius(20)
                    }
                }
                
                NavigationLink(destination: AboutDestination(), tag: "AboutDestination", selection: $selection) {
                    Button(action: {
                        selection = "AboutDestination"
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
            SettingsDestination: { AnyView(EmptyView()) },
            AboutDestination: { AnyView(EmptyView()) }
        )
    }
}
