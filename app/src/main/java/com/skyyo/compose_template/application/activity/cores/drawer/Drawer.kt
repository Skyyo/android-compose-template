package com.skyyo.compose_template.application.activity.cores.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.skyyo.compose_template.application.Destination

@Composable
fun Drawer(
    screens: List<Destination>,
    selectedTab: Int,
    onTabClick: (index: Int, route: String) -> Unit
) {
    Column {
        screens.forEachIndexed { index, screen ->
            DrawerItem(
                title = stringResource(screen.resourceId),
                selected = index == selectedTab
            ) { onTabClick(index, screen.route) }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
