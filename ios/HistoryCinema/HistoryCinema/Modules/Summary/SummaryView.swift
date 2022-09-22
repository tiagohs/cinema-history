//
//  Summary.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import SwiftUI

struct SummaryView: View {
    
    @ObservedObject private var presenter: SummaryPresenter = SummaryWireframe.buildPresenter()
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    @State private var selection: String? = nil
    
    let mainTopic: MainTopicItem
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            ScrollView {
                LazyVStack {
                    SummaryHeader(mainTopic: mainTopic)
                    
                    VStack(alignment: .center) {
                        VStack {
                            Text(mainTopic.title!)
                                .frame(maxWidth: .infinity, alignment: .center)
                                .font(.oswaldBold(size: 32))
                                .multilineTextAlignment(.center)
                                .foregroundColor(Color.textPrimary)
                                .padding(.horizontal, 16)
                            
                            Text(mainTopic.description!)
                                .frame(maxWidth: .infinity, alignment: .center)
                                .font(.proximaNovaRegular(size: 16))
                                .multilineTextAlignment(.center)
                                .foregroundColor(Color.textPrimary)
                                .padding(.horizontal, 22)
                                .padding(.vertical, 8)
                            
                            NavigationLink(destination: presenter.presentHistoryPages(mainTopic, 0), tag: "HistoryPage", selection: $selection) {
                                Button(action: {
                                    selection = "HistoryPage"
                                }) {
                                    Text("Iniciar")
                                        .font(.proximaNovaRegular(size: 18))
                                        .padding(.horizontal, 22)
                                        .padding(.vertical, 12)
                                        .background(Color.cardBackground)
                                        .foregroundColor(Color.cardTextPrimary)
                                }
                            }
                            
                            
                            Divider()
                                .padding(.horizontal, 32)
                                .padding(.vertical, 22)
                            
                            Text("Sumário")
                                .textCase(.uppercase)
                                .frame(maxWidth: .infinity, alignment: .center)
                                .font(.heptaslabBold(size: 18))
                                .multilineTextAlignment(.center)
                                .foregroundColor(Color.textPrimary)
                                .padding(.horizontal, 16)
                                .padding(.bottom, 8)
                            
                            if (presenter.summaryList.count == 0) {
                                ProgressView()
                            }
                            
                            if !presenter.summaryList.isEmpty {
                                ForEach(0 ..< presenter.summaryList.count) { index in
                                    NavigationLink(destination: presenter.presentHistoryPages(mainTopic, index)) {
                                        SummaryItem(summaryModel: presenter.summaryList[index])
                                    }
                                    .listRowInsets(EdgeInsets())
                                }
                            }
                        }
                        .padding(.top, 20)
                    }
                }
            }
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "chevron.left")
                    .padding()
                    .background(Color.backgroundInverse)
                    .foregroundColor(Color.textInverse)
                    .clipShape(Circle())
            }
            .shadow(color: Color.backgroundInverse, radius: 5)
            .padding()
        }
        .onAppear {
            presenter.viewAppears()
            presenter.fetchSummaryBy(mainTopicItem: mainTopic)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct Summary_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopicItem = MainTopic.example(.history_cinema) as! MainTopicItem
        
        SummaryView(mainTopic: mainTopicItem)
    }
}
