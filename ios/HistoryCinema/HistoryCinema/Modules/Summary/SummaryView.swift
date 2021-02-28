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
            ScrollView {
                GeometryReader { geometry in
                    ZStack(alignment: .bottomTrailing) {
                        if geometry.frame(in: .global).minY <= 0 {
                            Image("img_cabinet")
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(width: geometry.size.width, height: geometry.size.height)
                                    .offset(y: geometry.frame(in: .global).minY/9)
                                    .clipped()
                                    .shadow(color: .black, radius: 5)
                            
                            SummaryQuoteView(quote: mainTopic.quote)
                                .offset(y: geometry.frame(in: .global).minY/2)
                        } else {
                            Image("img_cabinet")
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(width: geometry.size.width, height: geometry.size.height + geometry.frame(in: .global).minY)
                                    .clipped()
                                    .shadow(color: .black, radius: 5)
                                    .offset(y: -geometry.frame(in: .global).minY)
                            
                            SummaryQuoteView(quote: mainTopic.quote)
                                .offset(y: -geometry.frame(in: .global).minY)
                        }
                    }
                }
                .frame(height: 600)
                
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
                        
                        VStack {
                            HStack {
                                Text("Visionários")
                                    .textCase(.uppercase)
                                    .frame(maxWidth: .infinity, alignment: .center)
                                    .font(.proximaNovaBold(size: 20))
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(Color.black)
                                    .padding(.horizontal, 16)
                            }
                            
                            Text("Os primeiros inventores, sonhadores e artistas. Da invenção do cinetoscópio até a primeira exibição conhecida pública de um filme para um público pagante pelos irmãos Lumierè.")
                                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                                .font(.proximaNovaRegular(size: 16))
                                .multilineTextAlignment(.center)
                                .foregroundColor(Color.black)
                                .padding(.horizontal, 16)
                                .padding(.top, 5)
                        }
                        .padding(.vertical, 22)
                        
                        VStack {
                            HStack {
                                Text("Visionários")
                                    .textCase(.uppercase)
                                    .frame(maxWidth: .infinity, alignment: .center)
                                    .font(.proximaNovaBold(size: 20))
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(Color.black)
                                    .padding(.horizontal, 16)
                            }
                            
                            Text("Os primeiros inventores, sonhadores e artistas. Da invenção do cinetoscópio até a primeira exibição conhecida pública de um filme para um público pagante pelos irmãos Lumierè.")
                                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                                .font(.proximaNovaRegular(size: 16))
                                .multilineTextAlignment(.center)
                                .foregroundColor(Color.black)
                                .padding(.horizontal, 16)
                                .padding(.top, 5)
                        }
                        .padding(.vertical, 22)
                        
                        VStack {
                            HStack {
                                Text("Visionários")
                                    .textCase(.uppercase)
                                    .frame(maxWidth: .infinity, alignment: .center)
                                    .font(.proximaNovaBold(size: 20))
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(Color.black)
                                    .padding(.horizontal, 16)
                            }
                            
                            Text("Os primeiros inventores, sonhadores e artistas. Da invenção do cinetoscópio até a primeira exibição conhecida pública de um filme para um público pagante pelos irmãos Lumierè.")
                                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                                .font(.proximaNovaRegular(size: 16))
                                .multilineTextAlignment(.center)
                                .foregroundColor(Color.black)
                                .padding(.horizontal, 16)
                                .padding(.top, 5)
                        }
                        .padding(.vertical, 22)
                        
                        VStack {
                            HStack {
                                Text("Visionários")
                                    .textCase(.uppercase)
                                    .frame(maxWidth: .infinity, alignment: .center)
                                    .font(.proximaNovaBold(size: 20))
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(Color.black)
                                    .padding(.horizontal, 16)
                            }
                            
                            Text("Os primeiros inventores, sonhadores e artistas. Da invenção do cinetoscópio até a primeira exibição conhecida pública de um filme para um público pagante pelos irmãos Lumierè.")
                                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                                .font(.proximaNovaRegular(size: 16))
                                .multilineTextAlignment(.center)
                                .foregroundColor(Color.black)
                                .padding(.horizontal, 16)
                                .padding(.top, 5)
                        }
                        .padding(.vertical, 22)
                    }
                    .padding(.top, 20)
                }
            }
            .edgesIgnoringSafeArea(.top)
            .background(Color.white)
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "arrow.left")
                    .padding()
                    .background(Color.black)
                    .foregroundColor(Color.white)
                    .clipShape(Circle())
            }
            .shadow(color: .black, radius: 5)
            .padding()
        }
    }
}

struct Summary_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopicItem = MainTopic.example(.history_cinema) as! MainTopicItem
        
        SummaryView(mainTopic: mainTopicItem)
    }
}
