; background color
; backColor = aaaa96
; backColor = aa43d996
backColor= 0x242424
backImage= "background.jpg"

; line color
rectLineColor = 0xff303030
timeTextColor = 0xff999999

timeLineColor = 0xffffaf00
; default servo color
;defaultColor= 0xaf2b4085
defaultColor=af2a3f84
; servo side line color
sideColor= af2b4055

; if servoId clash, show side line color
errorColor = ffff0000

; if mouse pressed servo color
pressedColor =af2c60a5

; if move a servo show color
moveColor = af2c60a5

; if mouse enter a servo show color
enterColor= 0xaf2c60a5

; if show side line or not
isShowSide = true

; left rect color (beginAngle)
leftColor = 0xaf2b4085

; right rect color (endAngle)
rightColor =0xaf2b4085

; beginAngle text color
;beginAngleColor = ff111111
beginAngleColor = ffeeeeee

; endAngle text color
;endAngleColor = ff111111
endAngleColor = ffeeeeee

; servoId text color
servoIdColor = fffeaf03

enterSideColor = af2c60a5

; rect radius
radius = 6

;
playTime = 25

; wave range send to serial
waveMin = 500
waveMax = 2500

; angle range
angleMin = 0
angleMax = 180.0

; servoid range
beginServoId = 0
endServoId = 23

; serial protocol : support '\r','\n','\\','\t', and '\s' (space); %i is servoId, %v is pulse size
; it support \xx, xx is Two hexadecimal digits, like \ff, \0a
; protocol = "#%iP%v\r" 
protocol = "#%iP%v\0d"


