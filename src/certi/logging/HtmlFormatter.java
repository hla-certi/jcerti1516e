// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2008-2010 Andrej Pancik
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package certi.logging;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author aVe
 */
public class HtmlFormatter extends Formatter {

    public String format(LogRecord rec) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("<TR><TD>");

        if (rec.getLevel().intValue() > Level.INFO.intValue()) {
            stringBuffer.append("<B>");
            stringBuffer.append(rec.getLevel());
            stringBuffer.append("</B>");
        } else {
            stringBuffer.append(rec.getLevel());
        }
        stringBuffer.append("</TD><TD>");
        stringBuffer.append(new Date(rec.getMillis()) + "</TD><TD>" + this.formatMessage(rec));
        stringBuffer.append("<TD></TR>\n");

        return stringBuffer.toString();
    }

    @Override
    public String getHead(Handler h) {
        return "<HTML><HEAD><TITLE>CERTI LibRTI Java log</TITLE> CERTI simulation commenced on " + new Date() + "</HEAD><BODY><PRE><TABLE BORDER><TR><TH>Level</TH><TH>Time</TH><TH>Log Message</TH></TR>";
    }

    @Override
    public String getTail(Handler h) {
        return "</TABLE>\n</PRE></BODY>\n</HTML>\n";
    }
}
