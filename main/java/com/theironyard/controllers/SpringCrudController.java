package com.theironyard.controllers;

import com.theironyard.entities.Book;
import com.theironyard.entities.User;
import com.theironyard.services.BookRepository;
import com.theironyard.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class SpringCrudController {

    @Autowired
    BookRepository books;

    @Autowired
    UserRepository users;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        List<Book> bookList;
        if (userName != null) {
            User user = users.findFirstByName(userName);
            model.addAttribute("user", user);
        }
        bookList = (List<Book>) books.findAll();
        model.addAttribute("books", bookList);
        return "home";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String userName, String password) throws Exception {
        User user = users.findFirstByName(userName);
        if (user == null) {
            users.save(new User(userName, password));
            System.out.println("User is logged in");
        } else if (!user.verifyPassword(password)) {
            throw new Exception("Login Failed");
        }
        session.setAttribute("userName", userName);
        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(path = "/add-book", method = RequestMethod.POST)
    public String addBook(HttpSession session, String title, String author, int releaseYear, User user) {
        String userName = (String) session.getAttribute("userName");
        user = users.findFirstByName(userName);
        Book book = new Book(title, author, releaseYear, user);
        books.save(book);
        return "redirect:/";
    }

    @RequestMapping(path = "/edit", method = RequestMethod.GET)
    public String edit(Model model, HttpSession session, Integer id) {
        String userName = (String) session.getAttribute("userName");
        User user = users.findFirstByName(userName);
        if (user != null) {
            System.out.println("User is logged in");
            Book editBook = books.findOne(id);
            model.addAttribute("books", editBook);
            session.setAttribute("editBook", editBook);
        }
        return "edit-entry";
    }

    @RequestMapping(path = "/edit-entry", method = RequestMethod.POST)
    public String editEntry(HttpSession session, String title, String author, int releaseYear, User user, Integer id) {
        String userName = (String) session.getAttribute("userName");
        Book editBook = (Book) session.getAttribute("editBook");
            user = users.findFirstByName(userName);
            Book newBook = new Book(title, author, releaseYear, user);
            books.delete(editBook);
            books.save(newBook);
        return "redirect:/";
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public String delete(HttpSession session, Integer id) {
        String userName = (String) session.getAttribute("userName");
        User user = users.findFirstByName(userName);
        if (user != null){
            System.out.println("User is logged in");
            Book deleteBook = books.findOne(id);
            books.delete(deleteBook);
        }
        return "redirect:/";
    }

}
