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
    
    let mainTopic: MainTopicItem
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            List {
                SummaryHeader(mainTopic: mainTopic)
                    .listRowInsets(EdgeInsets())
                
                VStack(alignment: .center) {
                    VStack {
                        Text(mainTopic.title!)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .font(.oswaldBold(size: 32))
                            .multilineTextAlignment(.center)
                            .foregroundColor(Color.black)
                            .padding(.horizontal, 16)
                        
                        Text(mainTopic.description!)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.center)
                            .foregroundColor(Color.black)
                            .padding(.horizontal, 22)
                            .padding(.vertical, 8)
                        
                        Button(action: {
                            print("Ir Para Pages")
                        }) {
                            Text("Iniciar")
                                .font(.proximaNovaRegular(size: 18))
                                .padding(.horizontal, 22)
                                .padding(.vertical, 12)
                                .background(Color.black)
                                .foregroundColor(Color.white)
                        }
                        
                        Divider()
                            .padding(.horizontal, 32)
                            .padding(.vertical, 22)
                        
                        Text("Sumário")
                            .textCase(.uppercase)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .font(.heptaslabBold(size: 18))
                            .multilineTextAlignment(.center)
                            .foregroundColor(Color.black)
                            .padding(.horizontal, 16)
                            .padding(.bottom, 8)
                        
                        if (presenter.summaryList.count == 0) {
                            ProgressView()
                        }
                        
                        if !presenter.summaryList.isEmpty {
                            ForEach(0 ..< presenter.summaryList.count) { index in
                                NavigationLink(destination: presenter.presentHistoryPages(mainTopic)) {
                                    SummaryItem(summaryModel: presenter.summaryList[index])
                                }
                                .listRowInsets(EdgeInsets())
                            }
                        }
                    }
                    .padding(.top, 20)
                }
            }
            .edgesIgnoringSafeArea(.top)
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "chevron.left")
                    .padding()
                    .background(Color.black)
                    .foregroundColor(Color.white)
                    .clipShape(Circle())
            }
            .shadow(color: .black, radius: 5)
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
