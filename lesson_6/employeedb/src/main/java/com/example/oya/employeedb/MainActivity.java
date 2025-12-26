package com.example.oya.employeedb;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextSalary;
    private Button buttonAdd, buttonShowAll, buttonClear;
    private TextView textViewOutput, textViewCount;
    private AppDatabase database;
    private EmployeeDao employeeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        editTextSalary = findViewById(R.id.editTextSalary);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonShowAll = findViewById(R.id.buttonShowAll);
        buttonClear = findViewById(R.id.buttonClear);
        textViewOutput = findViewById(R.id.textViewOutput);
        textViewCount = findViewById(R.id.textViewCount);

        database = App.getInstance().getDatabase();
        employeeDao = database.employeeDao();

        updateEmployeeCount();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });

        buttonShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllEmployees();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOutput();
            }
        });

        addTestEmployees();
    }

    private void addEmployee() {
        String name = editTextName.getText().toString();
        String salaryStr = editTextSalary.getText().toString();

        if (name.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int salary = Integer.parseInt(salaryStr);

            Employee employee = new Employee();
            employee.name = name;
            employee.salary = salary;

            employeeDao.insert(employee);

            editTextName.setText("");
            editTextSalary.setText("");
            Toast.makeText(this, "Сотрудник добавлен", Toast.LENGTH_SHORT).show();

            updateEmployeeCount();
            showAllEmployees();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите корректную зарплату", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllEmployees() {
        List<Employee> employees = employeeDao.getAll();
        StringBuilder sb = new StringBuilder();

        if (employees.isEmpty()) {
            sb.append("Нет сотрудников в базе данных");
        } else {
            sb.append("Список сотрудников:\n\n");
            for (Employee employee : employees) {
                sb.append("ID: ").append(employee.id)
                        .append("\nИмя: ").append(employee.name)
                        .append("\nЗарплата: ").append(employee.salary)
                        .append(" руб.\n------------------------\n");
            }
        }

        textViewOutput.setText(sb.toString());
    }

    private void updateEmployeeCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = employeeDao.getAll().size();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewCount.setText("Сотрудников: " + count);
                    }
                });
            }
        }).start();
    }

    private void clearOutput() {
        textViewOutput.setText("Список сотрудников будет здесь...");
        Toast.makeText(this, "Список очищен", Toast.LENGTH_SHORT).show();
    }

    private void addTestEmployees() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (employeeDao.getAll().isEmpty()) {
                    Employee emp1 = new Employee();
                    emp1.name = "Иван Иванов";
                    emp1.salary = 50000;
                    employeeDao.insert(emp1);

                    Employee emp2 = new Employee();
                    emp2.name = "Петр Петров";
                    emp2.salary = 75000;
                    employeeDao.insert(emp2);

                    Employee emp3 = new Employee();
                    emp3.name = "Мария Сидорова";
                    emp3.salary = 60000;
                    employeeDao.insert(emp3);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateEmployeeCount();
                            Toast.makeText(MainActivity.this,
                                    "Добавлены тестовые сотрудники", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}