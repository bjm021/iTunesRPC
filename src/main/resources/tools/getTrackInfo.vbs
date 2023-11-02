Function Base64Encode(sText)
    Dim oXML, oNode
    Set oXML = CreateObject("Msxml2.DOMDocument.3.0")
    Set oNode = oXML.CreateElement("base64")
    oNode.dataType = "bin.base64"
    oNode.nodeTypedValue = Stream_StringToBinary(sText)
    Base64Encode = oNode.text
    Set oNode = Nothing
    Set oXML = Nothing
End Function
Function Base64Decode(ByVal vCode)
    Dim oXML, oNode
    Set oXML = CreateObject("Msxml2.DOMDocument.3.0")
    Set oNode = oXML.CreateElement("base64")
    oNode.dataType = "bin.base64"
    oNode.text = vCode
    Base64Decode = Stream_BinaryToString(oNode.nodeTypedValue)
    Set oNode = Nothing
    Set oXML = Nothing
End Function
Private Function Stream_StringToBinary(Text)
  Const adTypeText = 2
  Const adTypeBinary = 1
  Dim BinaryStream 'As New Stream
  Set BinaryStream = CreateObject("ADODB.Stream")
  BinaryStream.Type = adTypeText
  BinaryStream.CharSet = "UTF-8"
  BinaryStream.Open
  BinaryStream.WriteText Text
  BinaryStream.Position = 0
  BinaryStream.Type = adTypeBinary
  BinaryStream.Position = 0
  Stream_StringToBinary = BinaryStream.Read
  Set BinaryStream = Nothing
End Function
Private Function Stream_BinaryToString(Binary)
  Const adTypeText = 2
  Const adTypeBinary = 1
  Dim BinaryStream 'As New Stream
  Set BinaryStream = CreateObject("ADODB.Stream")
  BinaryStream.Type = adTypeBinary
  BinaryStream.Open
  BinaryStream.Write Binary
  BinaryStream.Position = 0
  BinaryStream.Type = adTypeText
  BinaryStream.CharSet = "UTF-8"
  Stream_BinaryToString = BinaryStream.ReadText
  Set BinaryStream = Nothing
End Function

Dim iTunes
Set iTunes = CreateObject("iTunes.Application")

Dim output
Dim encodedOutput

If iTunes.CurrentTrack is Nothing Then
    output =  "{""title"": """", ""artist"": """", ""album"": """", ""state"": ""0"", ""position"": ""0"", ""duration"": ""0""}"

    ' Encode the output as Base64

    encodedOutput = Base64Encode(output)
    WScript.Echo encodedOutput
Else

    output =  "{""title"": """ & iTunes.CurrentTrack.Name & """, ""artist"": """ & iTunes.CurrentTrack.Artist & """ , ""album"": """ & iTunes.CurrentTrack.Album & """, ""state"": """ & iTunes.PlayerState & """, ""position"": """ & iTunes.PlayerPosition & """, ""duration"": """ & iTunes.CurrentTrack.Duration & """}"

    ' Encode the output as Base64
    encodedOutput = Base64Encode(output)
    WScript.Echo encodedOutput

End If