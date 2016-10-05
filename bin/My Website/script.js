var defaultStyle = 1;

function swapStyle(){
    var newStyle = (defaultStyle % 2 == 0? "main.css":"main2.css");
    document.getElementById("css").setAttribute("href", newStyle);
   console.log("Yes.");
    defaultStyle++;
}