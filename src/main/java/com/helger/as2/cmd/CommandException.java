
package com.helger.as2.cmd;

import com.helger.as2lib.exception.OpenAS2Exception;

public class CommandException extends OpenAS2Exception
{
  public CommandException (final String sMsg)
  {
    super (sMsg);
  }
}
