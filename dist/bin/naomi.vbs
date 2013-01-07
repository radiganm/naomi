' @file     naomi.vbs
' @author   Mac Radigan
Option Explicit
Set WshShell = WScript.CreateObject("WScript.Shell")
Dim commandLine = "java -jar " & WScript.Path & "\..\" & "naomi.jar" & Join(Wscript.Arguments, "")
WshShell.Run(commandLine)
WScript.Quit
' *EOF*
