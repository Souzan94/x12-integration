
package com.helger.as2.cmd;

import com.helger.as2lib.IDynamicComponent;
import com.helger.as2lib.session.IAS2Session;

public interface ICommand extends IDynamicComponent
{
  void setDescription (String desc);

  String getDescription ();

  void setName (String name);

  String getName ();

  IAS2Session getSession ();

  void setUsage (String usage);

  String getUsage ();

  CommandResult execute (Object [] params);
}
