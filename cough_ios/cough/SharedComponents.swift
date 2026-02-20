//
//  SharedComponents.swift
//  cough
//
//  Created by Assistant on 7/19/25.
//

import SwiftUI

struct GlassmorphicCard: View {
    var body: some View {
        RoundedRectangle(cornerRadius: 25)
            .fill(.ultraThinMaterial)
            .overlay(
                RoundedRectangle(cornerRadius: 25)
                    .stroke(
                        LinearGradient(
                            colors: [.white.opacity(0.6), .white.opacity(0.2)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        lineWidth: 1
                    )
            )
            .shadow(color: .black.opacity(0.3), radius: 20, x: 0, y: 10)
    }
}