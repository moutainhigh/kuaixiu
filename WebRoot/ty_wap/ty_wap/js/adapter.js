;(function () {
    // 声明一个变量fontsize  获取HTMLDOM对象
    var
      fontsize = 0,
      doc = document.documentElement 
    function setFontSize() {
      // 获取HTML的宽度
      var docWidth = doc.clientWidth
      // 根据公式计算出来应该设置的字体大小
      if(docWidth > 750) {
        fontsize = 100
      }else{
        fontsize = 100 / 750 * docWidth
      }
      // 设置给HTML计算出的字体大小
      doc.style.fontSize = fontsize + "px"
    }
    // 获取对应的事件类型
    var sizeevent = "onresize" in window ? "resize" : "orientationchange"
    // 绑定事件
    window.addEventListener(sizeevent, setFontSize, false)
    window.addEventListener('DOMContentLoaded', setFontSize, false)
  }())