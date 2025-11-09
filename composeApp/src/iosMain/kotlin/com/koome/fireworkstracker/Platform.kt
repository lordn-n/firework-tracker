package com.koome.fireworkstracker

import platform.UIKit.UIDevice
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSNumber
import platform.Foundation.numberWithLong
import platform.Foundation.timeIntervalSince1970

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun formatTimestamp(timestamp: Long): String {
    val date = NSDate.dateWithTimeIntervalSince1970(timestamp.toDouble() / 1000)
    val formatter = NSDateFormatter().apply {
        dateStyle = NSDateFormatterMediumStyle
        timeStyle = NSDateFormatterShortStyle
    }
    return formatter.stringFromDate(date)
}
