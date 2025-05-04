method psp(x: int, y: int) returns (x': int, y': int)
    requires x > 0
    requires y > 0
    ensures x + y == 2 * x' + y'
    ensures (x' % 2 == 1) || (y' % 2 == 0)
{
    x' := 1;
    y' := x + y - 2;
}