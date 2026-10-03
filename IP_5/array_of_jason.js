emps = [
    {name: "ajay", salary: 30000},
    {name: "shailesh", salary: 40000},
    {name: "sachin", salary: 50000}
]
//THeofjaoejeo
console.log(emps)
total_salary = emps.reduce((sum, i) => sum + i.salary, 0)
console.log(`Total Salary: ` + total_salary)