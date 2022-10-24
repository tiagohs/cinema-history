//
//  TimelineContentView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import SwiftUI

struct TimelineContentView: View {
    
    let timelinePage: TimelinePage!
    let timeline: Timeline!
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    
    var body: some View {
        VStack {
            switch timeline.type {
            case .item:
                TimelineContentItemView(timelinePage: timelinePage, timelineItem: timeline as? TimelineItem, onClickLink: onClickLink)
            case .footer:
                TimelineContentFooterView(timelinePage: timelinePage, timelineFooter: timeline as? TimelineFooter)
            case .title:
                TimelineContentTitleView(timelinePage: timelinePage, timelineTitle: timeline as? TimelineTitle)
            default:
                AnyView(EmptyView())
            }
        }
    }
}

struct TimelineContentView_Previews: PreviewProvider {
    static var previews: some View {
        let timelinePage = TimelinePage.exampleTimelinePage
        let timelineTitle = Timeline.exampleTitle
        let timelineItem = Timeline.exampleItem
        let timelineFooter = Timeline.exampleFooter
        
        Group {
            ScrollView {
                TimelineContentView(timelinePage: timelinePage, timeline: timelineTitle)
                TimelineContentView(timelinePage: timelinePage, timeline: timelineItem)
                TimelineContentView(timelinePage: timelinePage, timeline: timelineFooter)
            }
            TimelineContentView(timelinePage: timelinePage, timeline: timelineTitle)
            TimelineContentView(timelinePage: timelinePage, timeline: timelineItem)
            TimelineContentView(timelinePage: timelinePage, timeline: timelineFooter)
        }
    }
}
