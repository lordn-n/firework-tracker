package com.koome.fireworkstracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions

object MainScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(
            ReportTab,
            tabDisposable = {
                TabDisposable(
                    navigator = it,
                    tabs = listOf(ReportTab, ReportsTab, MapTab)
                )
            }
        ) {
            Scaffold(
                content = {
                    Box(Modifier.padding(it)) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color(0xFF303030)
                    ) {
                        val tabNavigator = LocalTabNavigator.current
                        val koomeOrange = Color(0xFFF15A21)
                        val selectedColor = Color(0xFF1F2937)

                        NavigationBarItem(
                            selected = tabNavigator.current == ReportTab,
                            onClick = { tabNavigator.current = ReportTab },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Report"
                                )
                            },
                            label = { Text("Report") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = koomeOrange,
                                selectedTextColor = koomeOrange,
                                indicatorColor = selectedColor,
                                unselectedIconColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )
                        NavigationBarItem(
                            selected = tabNavigator.current == ReportsTab,
                            onClick = { tabNavigator.current = ReportsTab },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Reports"
                                )
                            },
                            label = { Text("Reports") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = koomeOrange,
                                selectedTextColor = koomeOrange,
                                indicatorColor = selectedColor,
                                unselectedIconColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )
                        NavigationBarItem(
                            selected = tabNavigator.current == MapTab,
                            onClick = { tabNavigator.current = MapTab },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Map"
                                )
                            },
                            label = { Text("Map") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = koomeOrange,
                                selectedTextColor = koomeOrange,
                                indicatorColor = selectedColor,
                                unselectedIconColor = Color.White,
                                unselectedTextColor = Color.White
                            )
                        )
                    }
                }
            )
        }
    }
}

object ReportTab : Tab {
    @Composable
    override fun Content() {
        FireworkTrackerScreen()
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 0u, title = "Report")
}

object ReportsTab : Tab {
    @Composable
    override fun Content() {
        ReportsScreen()
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 1u, title = "Reports")
}

object MapTab : Tab {
    @Composable
    override fun Content() {
        MapScreen()
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 2u, title = "Map")
}
