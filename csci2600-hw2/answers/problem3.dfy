method loopysqrt(n: int) returns (root: int)
  requires n > 0
  ensures root * root >= n && (root - 1) * (root - 1) < n
{
  root := 0;
  var a := n;
  while (a > 0)
    decreases a
    invariant a == n - root * root
    invariant root == 0 || (root - 1) * (root - 1) < n
  {
    root := root + 1;
    a := a - (2 * root - 1);
  }
}


// method {:main} Main() {
//   var result1 := loopysqrt(4);
//   print "loopysqrt(4) = ", result1, "\n";
  
//   var result2 := loopysqrt(25);
//   print "loopysqrt(25) = ", result2, "\n";
  
//   var result3 := loopysqrt(30);
//   print "loopysqrt(30) = ", result3, "\n";
// }