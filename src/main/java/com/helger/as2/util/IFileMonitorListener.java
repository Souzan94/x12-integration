
package com.helger.as2.util;

import java.io.File;

import javax.annotation.Nonnull;

public interface IFileMonitorListener
{
  void onFileMonitorEvent (@Nonnull FileMonitor aMonitor, @Nonnull File aFile, @Nonnull EFileMonitorEvent eEvent);
}
