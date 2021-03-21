//
//  Icon.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 15/03/21.
//

import SwiftUI

struct Icon: Shape {
    let name: UIBezierPath

    func path(in rect: CGRect) -> Path {
        let path = Path(name.cgPath)
       
        return path.scaled(toFit: rect)
    }
}

struct Icon_Previews: PreviewProvider {
    static var previews: some View {
        Icon(name: .quote)
            .fill(Color.black)
            .frame(width: 200, height: 200)
    }
}
