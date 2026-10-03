nums = [1, 2, 3, 4, 5]

console.log("First Element: " + nums[0])
console.log("Last Element: " + nums[nums.length - 1])
sum = 0
for(i = 0;i < nums.length; i++)
    sum += nums[i]
console.log("Sum: " + sum)
console.log("Average: " + sum/nums.length)


nums_sqr = nums.map(i => i * i)
console.log("Square of each number: " + nums_sqr)

nums_even = nums.filter(i => i % 2 == 0)
console.log("Even numbers: " + nums_even)

nums_sum = nums.reduce((add, i) => add + i)
console.log("Sum of numbers: " + nums_sum)
