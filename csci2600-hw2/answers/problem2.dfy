method altSum(n: int) returns (t: int)
  requires n >= 0
  ensures (n % 2 == 0 ==> t == n / 2) && (n % 2 == 1 ==> t == -1 * ((n + 1) / 2))
{
  var i := 0;
  t := 0;
  while i < n
    invariant 0 <= i <= n
    invariant t == (if i % 2 == 0 then i / 2 else -1 * ((i + 1) / 2))
    decreases n - i
  {
    i := i + 1;
    if i % 2 == 0 {
      t := t + i;
    } else {
      t := t - i;
    }
  }
}
