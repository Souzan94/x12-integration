
package com.helger.as2.util;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.datetime.PDTFactory;

public class FileMonitor
{
  private ICommonsList <IFileMonitorListener> m_aListeners;
  private LocalDateTime m_aLastModified;
  private File m_aFile;
  private Timer m_aTimer;
  private boolean m_bBusy;
  private int m_nIntervalSecs;

  public FileMonitor (final File file, final int nIntervalSecs)
  {
    super ();
    m_aFile = file;
    m_nIntervalSecs = nIntervalSecs;
    start ();
  }

  public void setBusy (final boolean busy)
  {
    m_bBusy = busy;
  }

  public boolean isBusy ()
  {
    return m_bBusy;
  }

  public void setFile (final File file)
  {
    m_aFile = file;
  }

  public File getFile ()
  {
    return m_aFile;
  }

  public String getFilename ()
  {
    if (getFile () != null)
    {
      return getFile ().getAbsolutePath ();
    }

    return null;
  }

  public void setInterval (final int nIntervalSecs)
  {
    m_nIntervalSecs = nIntervalSecs;
    restart ();
  }

  public int getInterval ()
  {
    return m_nIntervalSecs;
  }

  public void setLastModified (final LocalDateTime aLastModified)
  {
    m_aLastModified = aLastModified;
  }

  public LocalDateTime getLastModified ()
  {
    return m_aLastModified;
  }

  public void setListeners (@Nullable final ICommonsList <IFileMonitorListener> aListeners)
  {
    m_aListeners = aListeners;
  }

  @Nonnull
  public ICommonsList <IFileMonitorListener> getListeners ()
  {
    if (m_aListeners == null)
      m_aListeners = new CommonsArrayList <> ();
    return m_aListeners;
  }

  public void addListener (final IFileMonitorListener listener)
  {
    getListeners ().add (listener);
  }

  public void restart ()
  {
    stop ();
    start ();
  }

  public final void start ()
  {
    m_aTimer = getTimer ();
    m_aTimer.scheduleAtFixedRate (new TimerTick (), 0, getInterval () * 1000);
  }

  public void stop ()
  {
    if (m_aTimer != null)
    {
      m_aTimer.cancel ();
    }
  }

  protected boolean isModified ()
  {
    final LocalDateTime lastModified = getLastModified ();
    if (lastModified != null)
    {
      final LocalDateTime currentModified = PDTFactory.createLocalDateTime (getFile ().lastModified ());

      return !currentModified.equals (getLastModified ());
    }
    updateModified ();
    return false;
  }

  @Nonnull
  protected Timer getTimer ()
  {
    if (m_aTimer == null)
      m_aTimer = new Timer (true);
    return m_aTimer;
  }

  protected void updateListeners ()
  {
    if (isModified ())
    {
      updateModified ();
      updateListeners (EFileMonitorEvent.EVENT_MODIFIED);
    }
  }

  protected void updateListeners (@Nonnull final EFileMonitorEvent eEvent)
  {
    final ICommonsList <IFileMonitorListener> aListeners = getListeners ();
    for (final IFileMonitorListener aListener : aListeners)
      aListener.onFileMonitorEvent (this, getFile (), eEvent);
  }

  protected void updateModified ()
  {
    setLastModified (PDTFactory.createLocalDateTime (getFile ().lastModified ()));
  }

  private class TimerTick extends TimerTask
  {
    @Override
    public void run ()
    {
      if (!isBusy ())
      {
        setBusy (true);
        updateListeners ();
        setBusy (false);
      }
      else
      {
        updateListeners (EFileMonitorEvent.EVENT_MISSED_TICK);
      }
    }
  }
}
