Dim iTunes
Set iTunes = CreateObject("iTunes.Application")

If iTunes.CurrentTrack is Nothing Then
    WScript.Echo "No track currently playing"
Else
    ' Save the album artwork as an image file
        If Not iTunes.CurrentTrack.Artwork Is Nothing Then
            Dim artworkData
            Set artworkData = iTunes.CurrentTrack.Artwork
            Dim artwork
            Set artwork = artworkData.Item(1)
            Dim format ' 1 = JPEG, 2 = PNG, 3 = BMP
            If artwork.Format = 1 Then
                format = "jpg"
            ElseIf artwork.Format = 2 Then
                format = "png"
            ElseIf artwork.Format = 3 Then
                format = "bmp"
            End If

            WScript.Echo "{""format"": """ & format & """}"

            Dim outPath
            outPath = Left(WScript.ScriptFullName, InStrRev(WScript.ScriptFullName, "\") - 1) & "\tmp." & format

            artwork.SaveArtworkToFile outPath

        End If
End If
